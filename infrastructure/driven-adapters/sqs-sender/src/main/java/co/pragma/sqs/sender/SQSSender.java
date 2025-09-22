package co.pragma.sqs.sender;

import co.pragma.sns.publisher.SqsQueuesProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

@Log4j2
@RequiredArgsConstructor
@Component
public class SQSSender {
    private final SqsQueuesProperties properties;
    private final SqsAsyncClient sqsAsyncClient;
    private final ObjectMapper objectMapper;

    public <T> Mono<Void> sendEvent(String alias, T event) {
        return Mono.fromCallable(() -> objectMapper.writeValueAsString(event))
                .flatMap(json -> send(alias, json))
                .then();
    }

    private Mono<String> send(String alias, String message) {
        return Mono.fromCallable(() -> buildRequest(alias, message))
                .flatMap(request -> Mono.fromFuture(sqsAsyncClient.sendMessage(request)))
                .doOnNext(response -> log.debug("Message sent to {} queue: {}", alias, response.messageId()))
                .map(SendMessageResponse::messageId);
    }

    private SendMessageRequest buildRequest(String alias, String message) {
        return SendMessageRequest.builder()
                .queueUrl(properties.queues().get(alias))
                .messageBody(message)
                .build();
    }
}

