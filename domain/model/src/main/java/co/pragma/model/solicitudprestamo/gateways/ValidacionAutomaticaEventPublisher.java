package co.pragma.model.solicitudprestamo.gateways;

import co.pragma.model.solicitudprestamo.projection.SolicitudEvaluacionAutoEvent;
import reactor.core.publisher.Mono;

public interface ValidacionAutomaticaEventPublisher {
    Mono<Void> publish(SolicitudEvaluacionAutoEvent event);
}
