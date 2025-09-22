package co.pragma.sns.publisher;

import co.pragma.model.solicitudprestamo.gateways.SolicitudEvaluadaPublisher;
import co.pragma.model.solicitudprestamo.projection.SolicitudEvaluadaEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class SNSSolicitudEvaluadaEventPublisher implements SolicitudEvaluadaPublisher {
    private final SNSPublisher publisher;

    @Override
    public Mono<Void> publish(SolicitudEvaluadaEvent event) {
        return publisher.publishEvent("solicitudevaluada", event, "Solicitud de prestamo finalizada");
    }
}
