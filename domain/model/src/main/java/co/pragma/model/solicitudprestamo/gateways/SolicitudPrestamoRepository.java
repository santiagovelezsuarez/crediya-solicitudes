package co.pragma.model.solicitudprestamo.gateways;

import co.pragma.model.estadosolicitud.EstadoSolicitudCodigo;
import co.pragma.model.solicitudprestamo.SolicitudPrestamo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.UUID;

public interface SolicitudPrestamoRepository {

    Mono<SolicitudPrestamo> save(SolicitudPrestamo solicitudPrestamo);

    Mono<SolicitudPrestamo> findByCodigo(String codigo);

    Flux<SolicitudPrestamo> findByIdEstadoIn(List<Integer> estados, int page, int size);

    Mono<Void> markAsNotificado(String codigo, Boolean notificado);

    Flux<SolicitudPrestamo> findByIdClienteAndIdEstado(UUID idCliente, EstadoSolicitudCodigo estadoSolicitudCodigo);
}
