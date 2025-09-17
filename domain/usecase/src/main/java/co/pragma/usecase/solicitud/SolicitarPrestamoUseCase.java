package co.pragma.usecase.solicitud;

import co.pragma.exception.business.TipoPrestamoNotFoundException;
import co.pragma.model.cliente.Cliente;
import co.pragma.model.cliente.gateways.ClienteRepository;
import co.pragma.model.estadosolicitud.EstadoSolicitudCodigo;
import co.pragma.model.solicitudprestamo.command.SolicitarPrestamoCommand;
import co.pragma.model.solicitudprestamo.SolicitudPrestamo;
import co.pragma.model.solicitudprestamo.gateways.SolicitudPrestamoRepository;
import co.pragma.model.solicitudprestamo.gateways.ValidacionAutomaticaEventPublisher;
import co.pragma.model.solicitudprestamo.projection.PrestamoInfo;
import co.pragma.model.solicitudprestamo.projection.SolicitudInfo;
import co.pragma.model.solicitudprestamo.projection.SolicitudEvaluacionAutoEvent;
import co.pragma.model.tipoprestamo.TipoPrestamo;
import co.pragma.model.tipoprestamo.gateways.TipoPrestamoRepository;
import co.pragma.usecase.solicitud.businessrules.TipoPrestamoValidator;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class SolicitarPrestamoUseCase {

    private final SolicitudPrestamoRepository solicitudPrestamoRepository;
    private final TipoPrestamoRepository tipoPrestamoRepository;
    private final TipoPrestamoValidator tipoPrestamoValidator;
    private final ClienteRepository clienteRepository;
    private final ValidacionAutomaticaEventPublisher validacionAutomaticaEventPublisher;

    public Mono<SolicitudPrestamo> execute(SolicitarPrestamoCommand cmd) {
        return tipoPrestamoRepository.findByNombre(cmd.tipoPrestamo())
                .switchIfEmpty(Mono.error(new TipoPrestamoNotFoundException()))
                .flatMap(tipo -> tipoPrestamoValidator.validate(cmd)
                        .thenReturn(toInitialEntity(cmd, tipo))
                        .flatMap(solicitud -> {
                            if(tipo.estaValidacionAutomatica()){
                                solicitud.setEstado(EstadoSolicitudCodigo.PENDIENTE_VALIDACION_AUTOMATICA);
                                return solicitudPrestamoRepository.save(solicitud)
                                        .flatMap(savedSolicitud -> publicarEvento(savedSolicitud).thenReturn(savedSolicitud));
                            } else {
                                solicitud.setEstado(EstadoSolicitudCodigo.PENDIENTE_REVISION);
                                return solicitudPrestamoRepository.save(solicitud);
                            }
                        })
                );
    }

    private SolicitudPrestamo toInitialEntity(SolicitarPrestamoCommand cmd, TipoPrestamo tipoPrestamo) {
        return SolicitudPrestamo.builder()
                .idCliente(UUID.fromString(cmd.idCliente()))
                .idTipoPrestamo(tipoPrestamo.getId())
                .monto(cmd.monto())
                .plazoEnMeses(cmd.plazoEnMeses())
                .tasaInteres(tipoPrestamo.getTasaInteres())
                .estado(EstadoSolicitudCodigo.PENDIENTE_REVISION)
                .codigo(generarCodigo())
                .tasaInteres(tipoPrestamo.getTasaInteres())
                .build();
    }

    private String generarCodigo() {
        String anio = String.valueOf(java.time.Year.now().getValue());
        String mes = String.format("%02d", java.time.LocalDate.now().getMonthValue());
        String dia = String.format("%02d", java.time.LocalDate.now().getDayOfMonth());
        String sufijo = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "SP-" + anio + mes + dia + "-" + sufijo;
    }

    private Mono<Void> publicarEvento(SolicitudPrestamo solicitud) {
        // TODO: Cambiar a cliente.email() una vez la cuenta de SES salga de sandbox
        String emailCliente = "santiago.velezs@autonoma.edu.co"; /* AWS Sandbox verified email */

        Mono<Cliente> clienteMono = clienteRepository.findById(solicitud.getIdCliente());
        Mono<TipoPrestamo> tipoMono = tipoPrestamoRepository.findById(String.valueOf(solicitud.getIdTipoPrestamo()));
        Mono<List<PrestamoInfo>> prestamosActivosMono = solicitudPrestamoRepository
                .findByIdClienteAndIdEstado(solicitud.getIdCliente(), EstadoSolicitudCodigo.APROBADA)
                .map(this::toPrestamoInfo)
                .collectList();

        return Mono.zip(clienteMono, tipoMono, prestamosActivosMono)
                .flatMap(tuple -> {
                    Cliente cliente = tuple.getT1();
                    TipoPrestamo tipo = tuple.getT2();
                    List<PrestamoInfo> prestamosActivos = tuple.getT3();

                    SolicitudInfo solicitudInfo = SolicitudInfo.builder()
                            .id(solicitud.getId())
                            .codigo(solicitud.getCodigo())
                            .monto(solicitud.getMonto())
                            .plazoEnMeses(solicitud.getPlazoEnMeses())
                            .tipoPrestamoId(solicitud.getIdTipoPrestamo())
                            .tasaInteresAnual(tipo.getTasaInteres())
                            .build();

                    var event = SolicitudEvaluacionAutoEvent.builder()
                            .solicitud(solicitudInfo)
                            .cliente(cliente)
                            .prestamosActivos(prestamosActivos)
                            .build();

                    return validacionAutomaticaEventPublisher.publish(event)
                            .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                                    .maxBackoff(Duration.ofSeconds(10)))
                            .timeout(Duration.ofSeconds(15));
                });
    }

    private PrestamoInfo toPrestamoInfo(SolicitudPrestamo solicitud) {
        return PrestamoInfo.builder()
                .monto(solicitud.getMonto())
                .plazoEnMeses(solicitud.getPlazoEnMeses())
                .tasaInteresAnual(solicitud.getTasaInteres())
                .build();
    }
}
