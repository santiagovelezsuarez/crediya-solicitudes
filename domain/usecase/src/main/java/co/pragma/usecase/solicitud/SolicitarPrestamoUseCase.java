package co.pragma.usecase.solicitud;

import co.pragma.exception.business.TipoPrestamoNotFoundException;
import co.pragma.model.estadosolicitud.EstadoSolicitudCodigo;
import co.pragma.model.solicitudprestamo.SolicitarPrestamoCommand;
import co.pragma.model.solicitudprestamo.SolicitudPrestamo;
import co.pragma.model.solicitudprestamo.gateways.SolicitudPrestamoRepository;
import co.pragma.model.tipoprestamo.TipoPrestamo;
import co.pragma.model.tipoprestamo.gateways.TipoPrestamoRepository;
import co.pragma.usecase.solicitud.businessrules.TipoPrestamoValidator;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import java.util.UUID;

@RequiredArgsConstructor
public class SolicitarPrestamoUseCase {

    private final SolicitudPrestamoRepository solicitudPrestamoRepository;
    private final TipoPrestamoRepository tipoPrestamoRepository;
    private final TipoPrestamoValidator tipoPrestamoValidator;

    public Mono<SolicitudPrestamo> execute(SolicitarPrestamoCommand cmd) {
        return tipoPrestamoRepository.findByNombre(cmd.tipoPrestamo())
                .switchIfEmpty(Mono.error(new TipoPrestamoNotFoundException()))
                .flatMap(tipo -> tipoPrestamoValidator.validate(cmd)
                        .thenReturn(toInitialEntity(cmd, tipo)))
                .flatMap(solicitudPrestamoRepository::save);
    }

    private SolicitudPrestamo toInitialEntity(SolicitarPrestamoCommand cmd, TipoPrestamo tipoPrestamo) {
        return SolicitudPrestamo.builder()
                .idCliente(UUID.fromString(cmd.idCliente()))
                .idTipoPrestamo(tipoPrestamo.getId())
                .monto(cmd.monto())
                .plazoEnMeses(cmd.plazoEnMeses())
                .estado(EstadoSolicitudCodigo.PENDIENTE_REVISION)
                .codigo(generarCodigo())
                .build();
    }

    private String generarCodigo() {
        String anio = String.valueOf(java.time.Year.now().getValue());
        String mes = String.format("%02d", java.time.LocalDate.now().getMonthValue());
        String dia = String.format("%02d", java.time.LocalDate.now().getDayOfMonth());
        String sufijo = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String codigo = "SP-" + anio + mes + dia + "-" + sufijo;
        return codigo;
    }
}
