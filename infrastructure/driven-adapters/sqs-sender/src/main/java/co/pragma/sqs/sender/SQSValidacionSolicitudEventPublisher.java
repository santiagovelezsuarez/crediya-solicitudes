package co.pragma.sqs.sender;

import co.pragma.model.solicitudprestamo.gateways.ValidacionAutomaticaEventPublisher;
import co.pragma.model.solicitudprestamo.projection.SolicitudEvaluacionAutoEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class SQSValidacionSolicitudEventPublisher implements ValidacionAutomaticaEventPublisher {
    private final SQSSender sender;

    @Override
    public Mono<Void> publish(SolicitudEvaluacionAutoEvent event) {
        return sender.sendEvent("validacionauto", event);
    }
}