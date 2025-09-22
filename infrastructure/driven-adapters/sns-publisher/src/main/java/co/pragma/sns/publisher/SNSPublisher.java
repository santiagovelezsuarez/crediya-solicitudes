package co.pragma.sns.publisher;

import co.pragma.sqs.sender.config.SnsTopicsProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sns.SnsAsyncClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

@Log4j2
@RequiredArgsConstructor
@Component
public class SNSPublisher {
    private final SnsTopicsProperties properties;
    private final SnsAsyncClient snsAsyncClient;
    private final ObjectMapper objectMapper;

    public <T> Mono<Void> publishEvent(String topicAlias, T event) {
        return publishEvent(topicAlias, event, null);
    }

    public <T> Mono<Void> publishEvent(String topicAlias, T event, String subject) {
        return Mono.fromCallable(() -> objectMapper.writeValueAsString(event))
                .flatMap(json -> publish(topicAlias, json, subject))
                .then();
    }

    private Mono<String> publish(String topicAlias, String message, String subject) {
        return Mono.fromCallable(() -> buildRequest(topicAlias, message, subject))
                .flatMap(request -> Mono.fromFuture(snsAsyncClient.publish(request)))
                .doOnNext(response -> log.debug("Message published to {} topic: {}", topicAlias, response.messageId()))
                .map(PublishResponse::messageId);
    }

    private PublishRequest buildRequest(String topicAlias, String message, String subject) {
        var builder = PublishRequest.builder()
                .topicArn(properties.topics().get(topicAlias))
                .message(message);

        if (subject != null) {
            builder.subject(subject);
        }

        return builder.build();
    }
}
