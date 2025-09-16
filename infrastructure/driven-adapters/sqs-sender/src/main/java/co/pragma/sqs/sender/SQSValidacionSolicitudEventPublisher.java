package co.pragma.sqs.sender;

import co.pragma.model.solicitudprestamo.gateways.ValidacionAutomaticaEventPublisher;
import co.pragma.model.solicitudprestamo.projection.SolicitudValidacionAutoEvent;
import co.pragma.sqs.sender.config.SqsQueuesProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

@Service
@Log4j2
@RequiredArgsConstructor
public class SQSValidacionSolicitudEventPublisher implements ValidacionAutomaticaEventPublisher {
    private final SqsQueuesProperties properties;
    private final SqsAsyncClient sqsAsyncClient;
    private final ObjectMapper objectMapper;

    private String getQueueUrl(String alias) {
        return properties.queues().get(alias);
    }

    public Mono<String> send(String alias, String message) {
        return Mono.fromCallable(() -> buildRequest(alias, message))
                .flatMap(request -> Mono.fromFuture(sqsAsyncClient.sendMessage(request)))
                .doOnNext(response -> log.debug("Message sent to {} queue: {}", alias, response.messageId()))
                .map(SendMessageResponse::messageId);
    }

    private SendMessageRequest buildRequest(String alias, String message) {
        return SendMessageRequest.builder()
                .queueUrl(getQueueUrl(alias))
                .messageBody(message)
                .build();
    }

    @Override
    public Mono<Void> publish(SolicitudValidacionAutoEvent event) {
        return Mono.fromCallable(() -> objectMapper.writeValueAsString(event))
                .flatMap(json -> send("validacionauto", json))
                .then();
    }
}
