package co.pragma.sns.publisher;

import co.pragma.sqs.sender.config.SnsTopicsProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;
import software.amazon.awssdk.services.sns.SnsAsyncClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SNSPublisherTest {

    @Mock
    private SnsTopicsProperties properties;

    @Mock
    private SnsAsyncClient snsAsyncClient;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private SNSPublisher snsPublisher;

    private static final String TOPIC_ALIAS = "test-topic";
    private static final String TOPIC_ARN = "arn:aws:sns:us-east-1:123456789012:test-topic";
    private static final String SUBJECT = "Test Subject";
    private static final String JSON_MESSAGE = "{\"key\":\"value\"}";

    private TestEvent testEvent;

    private record TestEvent(String key, String value) {
    }

    @BeforeEach
    void setUp() {
        testEvent = new TestEvent("key", "value");
    }

    @Test
    void publishEventSuccess() throws JsonProcessingException {
        ArgumentCaptor<PublishRequest> requestCaptor = ArgumentCaptor.forClass(PublishRequest.class);

        when(properties.topics()).thenReturn(Map.of(TOPIC_ALIAS, TOPIC_ARN));
        when(objectMapper.writeValueAsString(testEvent)).thenReturn(JSON_MESSAGE);
        when(snsAsyncClient.publish(requestCaptor.capture())).thenReturn(
                CompletableFuture.completedFuture(PublishResponse.builder().messageId("msg-123").build())
        );

        StepVerifier.create(snsPublisher.publishEvent(TOPIC_ALIAS, testEvent, SUBJECT))
                .verifyComplete();

        PublishRequest capturedRequest = requestCaptor.getValue();
        assertEquals(TOPIC_ARN, capturedRequest.topicArn());
        assertEquals(JSON_MESSAGE, capturedRequest.message());
        assertEquals(SUBJECT, capturedRequest.subject());

        verify(objectMapper).writeValueAsString(testEvent);
        verify(snsAsyncClient).publish(any(PublishRequest.class));
    }

    @Test
    void publishEventSuccessNullSubject() throws JsonProcessingException {
        ArgumentCaptor<PublishRequest> requestCaptor = ArgumentCaptor.forClass(PublishRequest.class);
        when(properties.topics()).thenReturn(Map.of(TOPIC_ALIAS, TOPIC_ARN));
        when(objectMapper.writeValueAsString(testEvent)).thenReturn(JSON_MESSAGE);
        when(snsAsyncClient.publish(requestCaptor.capture())).thenReturn(
                CompletableFuture.completedFuture(PublishResponse.builder().messageId("msg-456").build())
        );

        StepVerifier.create(snsPublisher.publishEvent(TOPIC_ALIAS, testEvent, null)) // Subject es null
                .verifyComplete();

        PublishRequest capturedRequest = requestCaptor.getValue();
        assertEquals(TOPIC_ARN, capturedRequest.topicArn());
        assertEquals(JSON_MESSAGE, capturedRequest.message());
        assertNull(capturedRequest.subject());
    }

    @Test
    void publishEventFailsOnSerializationError() throws JsonProcessingException {
        when(objectMapper.writeValueAsString(testEvent))
                .thenThrow(new JsonProcessingException("Serialization error") {
                });


        StepVerifier.create(snsPublisher.publishEvent(TOPIC_ALIAS, testEvent, SUBJECT))
                .expectError(JsonProcessingException.class)
                .verify();

        verifyNoInteractions(snsAsyncClient);
    }

    @Test
    void publishEventFailsOnSnsClientError() throws JsonProcessingException {
        RuntimeException snsException = new RuntimeException("AWS SNS Error");
        when(properties.topics()).thenReturn(Map.of(TOPIC_ALIAS, TOPIC_ARN));
        when(objectMapper.writeValueAsString(testEvent)).thenReturn(JSON_MESSAGE);
        when(snsAsyncClient.publish(any(PublishRequest.class)))
                .thenReturn(CompletableFuture.failedFuture(snsException));

        StepVerifier.create(snsPublisher.publishEvent(TOPIC_ALIAS, testEvent, SUBJECT))
                .expectErrorMatches(throwable -> throwable.equals(snsException))
                .verify();
    }
}
