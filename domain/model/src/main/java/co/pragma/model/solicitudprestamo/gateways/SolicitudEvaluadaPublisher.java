package co.pragma.model.solicitudprestamo.gateways;

import co.pragma.model.solicitudprestamo.projection.SolicitudEvaluadaEvent;
import reactor.core.publisher.Mono;

public interface SolicitudEvaluadaPublisher {
    Mono<Void> publish(SolicitudEvaluadaEvent event);
}
