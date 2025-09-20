package co.pragma.sqs.sender;

import co.pragma.model.solicitudprestamo.gateways.ResultadoSolicitudPublisher;
import co.pragma.model.solicitudprestamo.projection.EstadoSolicitudEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class SQSResultadoSolicitudEventPublisher implements ResultadoSolicitudPublisher {
    private final SQSSender sender;

    @Override
    public Mono<Void> publish(EstadoSolicitudEvent event) {
        return sender.sendEvent("resultadoasesor", event);
    }
}

