package co.pragma.usecase.solicitud;

import co.pragma.model.cliente.Cliente;
import co.pragma.model.cliente.gateways.ClienteRepository;
import co.pragma.model.estadosolicitud.EstadoSolicitudCodigo;
import co.pragma.model.solicitudprestamo.SolicitudPrestamo;
import co.pragma.model.solicitudprestamo.gateways.SolicitudPrestamoRepository;
import co.pragma.model.solicitudprestamo.projection.SolicitudPrestamoRevision;
import co.pragma.model.tipoprestamo.TipoPrestamo;
import co.pragma.model.tipoprestamo.projection.TipoPrestamoInfo;
import co.pragma.model.tipoprestamo.gateways.TipoPrestamoRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class ListarSolicitudesRevisionManualUseCase {

    private final SolicitudPrestamoRepository solicitudRepository;
    private final TipoPrestamoRepository tipoPrestamoRepository;
    private final ClienteRepository clienteRepository;

    private static final List<Integer> ESTADOS_REVISION = List.of(
            EstadoSolicitudCodigo.PENDIENTE_REVISION.getCode(),
            EstadoSolicitudCodigo.RECHAZADA.getCode(),
            EstadoSolicitudCodigo.REVISION_MANUAL.getCode()
    );

    public Mono<List<SolicitudPrestamoRevision>> execute(int page, int size) {
        return solicitudRepository.findByIdEstadoIn(ESTADOS_REVISION, page, size)
                .collectList()
                .flatMap(this::resolveSolicitudesWithRelations);
    }

    private Mono<List<SolicitudPrestamoRevision>> resolveSolicitudesWithRelations(List<SolicitudPrestamo> solicitudes) {
        if (solicitudes.isEmpty())
            return Mono.just(Collections.emptyList());

        List<UUID> userIds = solicitudes.stream()
                .map(SolicitudPrestamo::getIdCliente)
                .distinct()
                .toList();

        List<UUID> tipoIds = solicitudes.stream()
                .map(SolicitudPrestamo::getIdTipoPrestamo)
                .distinct()
                .toList();

        Mono<Map<UUID, Cliente>> clientesMono = clienteRepository.findByIdIn(userIds)
                .collectMap(Cliente::getId, cliente -> cliente);

        Mono<Map<UUID, TipoPrestamo>> tiposMono = tipoPrestamoRepository.findByIdIn(tipoIds)
                .collectMap(TipoPrestamo::getId, tipo -> tipo);

        return Mono.zip(clientesMono, tiposMono)
                .map(tuple -> {
                    Map<UUID, Cliente> clientes = tuple.getT1();
                    Map<UUID, TipoPrestamo> tipos = tuple.getT2();

                    return solicitudes.stream()
                            .map(solicitud -> {
                                Cliente cliente = clientes.get(solicitud.getIdCliente());
                                TipoPrestamo tipo = tipos.get(solicitud.getIdTipoPrestamo());
                                return toView(solicitud, cliente, tipo);
                            })
                            .toList();
                });
    }

    private SolicitudPrestamoRevision toView(SolicitudPrestamo solicitud, Cliente cliente, TipoPrestamo tipoPrestamo) {
        return SolicitudPrestamoRevision.builder()
                .id(solicitud.getId())
                .monto(solicitud.getMonto())
                .plazoEnMeses(solicitud.getPlazoEnMeses())
                .tipoPrestamo(tipoPrestamo.getNombre())
                .estado(EstadoSolicitudCodigo.valueOf(solicitud.getEstado().name()))
                .tasaInteres(tipoPrestamo.getTasaInteres())
                .emailCliente(cliente.getEmail())
                .nombreCliente(cliente.getFullName())
                .salarioBase(cliente.getSalarioBase())
                .montoMensualSolicitud(solicitud.calcularCuota(tipoPrestamo.getTasaInteres()))
                .build();
    }
}
