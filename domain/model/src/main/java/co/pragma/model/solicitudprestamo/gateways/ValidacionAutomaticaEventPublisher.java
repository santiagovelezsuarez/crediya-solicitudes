package co.pragma.model.solicitudprestamo.gateways;

import co.pragma.model.solicitudprestamo.projection.SolicitudValidacionAutoEvent;
import reactor.core.publisher.Mono;

public interface ValidacionAutomaticaEventPublisher {
    Mono<Void> publish(SolicitudValidacionAutoEvent event);
}
