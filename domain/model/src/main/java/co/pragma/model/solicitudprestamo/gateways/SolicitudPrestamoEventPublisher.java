package co.pragma.model.solicitudprestamo.gateways;

import co.pragma.model.solicitudprestamo.projection.EstadoSolicitudEvent;
import reactor.core.publisher.Mono;

public interface SolicitudPrestamoEventPublisher {
    Mono<Void> publishEstadoActualizado(EstadoSolicitudEvent event);
}
