package co.pragma.usecase.solicitud;

import co.pragma.model.estadosolicitud.EstadoSolicitudCodigoEnum;
import co.pragma.model.solicitudprestamo.SolicitudPrestamo;
import co.pragma.model.solicitudprestamo.command.AprobarSolicitudCommand;
import co.pragma.model.solicitudprestamo.projection.DecisionSolicitudPrestamo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AprobarSolicitudPrestamoUseCaseTest {

    @Mock
    private ProcesarDecisionSolicitudUseCase procesarDecisionSolicitudUseCase;

    @InjectMocks
    private AprobarSolicitudPrestamoUseCase useCase;

    private AprobarSolicitudCommand cmd;
    private SolicitudPrestamo solicitud;

    @BeforeEach
    void setUp() {
        cmd = new AprobarSolicitudCommand("SP-12345", EstadoSolicitudCodigoEnum.APROBADA);
        solicitud = SolicitudPrestamo.builder()
                .id(UUID.randomUUID())
                .codigo("SP-12345")
                .estado(EstadoSolicitudCodigoEnum.APROBADA)
                .build();
    }

    @Test
    void shouldDelegateToProcesarDecisionSolicitudUseCase() {
        when(procesarDecisionSolicitudUseCase.execute(any(DecisionSolicitudPrestamo.class)))
                .thenReturn(Mono.just(solicitud));

        StepVerifier.create(useCase.execute(cmd))
                .expectNext(solicitud)
                .verifyComplete();

        ArgumentCaptor<DecisionSolicitudPrestamo> captor = ArgumentCaptor.forClass(DecisionSolicitudPrestamo.class);
        verify(procesarDecisionSolicitudUseCase).execute(captor.capture());

        DecisionSolicitudPrestamo captured = captor.getValue();
        assertThat(captured.getCodigoSolicitud()).isEqualTo(cmd.codigoSolicitud());
        assertThat(captured.getDecision()).isEqualTo(cmd.estado());
    }

    @Test
    void shouldPropagateErrorFromProcesarDecisionSolicitudUseCase() {
        when(procesarDecisionSolicitudUseCase.execute(any(DecisionSolicitudPrestamo.class)))
                .thenReturn(Mono.error(new RuntimeException("Error en procesamiento")));

        StepVerifier.create(useCase.execute(cmd))
                .expectErrorMatches(error -> error instanceof RuntimeException &&
                        error.getMessage().equals("Error en procesamiento"))
                .verify();
    }
}
