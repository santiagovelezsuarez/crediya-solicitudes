package co.pragma.sns.publisher;

import co.pragma.model.solicitudprestamo.projection.SolicitudEvaluadaEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SNSSolicitudEvaluadaEventPublisherTest {

    @Mock
    private SNSPublisher publisher;

    @InjectMocks
    private SNSSolicitudEvaluadaEventPublisher solicitudEvaluadaPublisher;

    private SolicitudEvaluadaEvent testEvent;

    @BeforeEach
    void setUp() {
        testEvent = SolicitudEvaluadaEvent.builder()
                .codigoSolicitud("SP-12345")
                .estado("APROBADA")
                .emailCliente("test@example.com")
                .build();
    }

    @Test
    void publishShouldCallPublisherWithCorrectParametersAndSucceed() {
        String expectedTopicAlias = "solicitudevaluada";
        String expectedSubject = "Solicitud de prestamo finalizada";

        when(publisher.publishEvent(expectedTopicAlias, testEvent, expectedSubject))
                .thenReturn(Mono.empty());

        StepVerifier.create(solicitudEvaluadaPublisher.publish(testEvent))
                .verifyComplete();

        verify(publisher, times(1))
                .publishEvent(expectedTopicAlias, testEvent, expectedSubject);
    }

    @Test
    void publishShouldPropagateErrorWhenPublisherFails() {
        RuntimeException simulatedError = new RuntimeException("Error en SNS");

        when(publisher.publishEvent(anyString(), any(SolicitudEvaluadaEvent.class), anyString()))
                .thenReturn(Mono.error(simulatedError));

        StepVerifier.create(solicitudEvaluadaPublisher.publish(testEvent))
                .expectErrorMatches(throwable -> throwable.equals(simulatedError))
                .verify();

        verify(publisher, times(1))
                .publishEvent("solicitudevaluada", testEvent, "Solicitud de prestamo finalizada");
    }
}
