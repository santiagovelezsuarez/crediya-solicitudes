package co.pragma.model.solicitudprestamo.gateways;

import co.pragma.model.solicitudprestamo.projection.EstadoSolicitudEvent;
import reactor.core.publisher.Mono;

public interface ResultadoSolicitudPublisher {
    Mono<Void> publish(EstadoSolicitudEvent event);
}
