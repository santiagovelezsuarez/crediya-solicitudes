package co.pragma.sqs.listener;

import co.pragma.model.solicitudprestamo.projection.DecisionSolicitudPrestamo;
import co.pragma.usecase.solicitud.ActualizarEstadoSolicitudUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.model.Message;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class SQSResultadoEvaluacionAutoProcessor implements Function<Message, Mono<Void>> {

    private final ObjectMapper objectMapper;
    private final ActualizarEstadoSolicitudUseCase actualizarEstadoSolicitudUseCase;

    @Override
    public Mono<Void> apply(Message message) {
        log.debug("Mensaje recibido desde SQSResultadoEvaluacionAuto: {}", message.body());
        return Mono.fromCallable(() -> objectMapper.readValue(message.body(), DecisionSolicitudPrestamo.class))
                .doOnNext(resultado -> log.trace("Procesando resultado para solicitud {}", resultado.getCodigoSolicitud()))
                .flatMap(actualizarEstadoSolicitudUseCase::execute)
                .doOnSuccess(v -> log.info("Actualización de estado completada"))
                .doOnError(e -> log.error("Error procesando mensaje SQS: {}", message.body(), e))
                .then();
    }
}
