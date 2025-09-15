package co.pragma.usecase.solicitud;

import co.pragma.model.cliente.projection.ClienteInfo;
import co.pragma.model.cliente.gateways.UsuarioPort;
import co.pragma.model.estadosolicitud.EstadoSolicitudCodigo;
import co.pragma.model.solicitudprestamo.SolicitudPrestamo;
import co.pragma.model.solicitudprestamo.gateways.SolicitudPrestamoRepository;
import co.pragma.model.solicitudprestamo.projection.SolicitudPrestamoRevision;
import co.pragma.model.tipoprestamo.projection.TipoPrestamoInfo;
import co.pragma.model.tipoprestamo.gateways.TipoPrestamoRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ListarSolicitudesRevisionManualUseCase {

    private final SolicitudPrestamoRepository solicitudRepository;
    private final TipoPrestamoRepository tipoPrestamoRepository;
    private final UsuarioPort usuarioPort;

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
                .collect(Collectors.toList());

        List<UUID> tipoIds = solicitudes.stream()
                .map(SolicitudPrestamo::getIdTipoPrestamo)
                .distinct()
                .collect(Collectors.toList());

        Mono<Map<UUID, ClienteInfo>> clientesMono = usuarioPort.getClientesByIdIn(userIds)
                .collectMap(ClienteInfo::id, cliente -> cliente);

        Mono<Map<UUID, TipoPrestamoInfo>> tiposMono = tipoPrestamoRepository.findByIdIn(tipoIds)
                .collectMap(TipoPrestamoInfo::id, tipo -> tipo);

        return Mono.zip(clientesMono, tiposMono)
                .map(tuple -> {
                    Map<UUID, ClienteInfo> clientes = tuple.getT1();
                    Map<UUID, TipoPrestamoInfo> tipos = tuple.getT2();

                    return solicitudes.stream()
                            .map(solicitud -> {
                                ClienteInfo cliente = clientes.get(solicitud.getIdCliente());
                                TipoPrestamoInfo tipo = tipos.get(solicitud.getIdTipoPrestamo());
                                return toView(solicitud, cliente, tipo);
                            })
                            .collect(Collectors.toList());
                });
    }

    private SolicitudPrestamoRevision toView(SolicitudPrestamo solicitud, ClienteInfo cliente, TipoPrestamoInfo tipoPrestamoInfo) {
        return SolicitudPrestamoRevision.builder()
                .id(solicitud.getId())
                .monto(solicitud.getMonto())
                .plazoEnMeses(solicitud.getPlazoEnMeses())
                .tipoPrestamo(tipoPrestamoInfo.nombre())
                .estado(EstadoSolicitudCodigo.valueOf(solicitud.getEstado().name()))
                .tasaInteres(tipoPrestamoInfo.tasaInteres())
                .emailCliente(cliente.email())
                .nombreCliente(cliente.nombre())
                .salarioBase(cliente.salarioBase())
                .montoMensualSolicitud(solicitud.calcularCuota(tipoPrestamoInfo.tasaInteres()))
                .build();
    }
}
