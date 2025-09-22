package co.pragma.usecase.solicitud;

import co.pragma.exception.business.SolicitudAlreadyProcessedException;
import co.pragma.exception.business.SolicitudPrestamoNotFound;
import co.pragma.model.cliente.Cliente;
import co.pragma.model.cliente.gateways.ClienteRepository;
import co.pragma.model.estadosolicitud.EstadoSolicitudCodigo;
import co.pragma.model.solicitudprestamo.SolicitudPrestamo;
import co.pragma.model.solicitudprestamo.gateways.SolicitudEvaluadaPublisher;
import co.pragma.model.solicitudprestamo.gateways.SolicitudPrestamoRepository;
import co.pragma.model.solicitudprestamo.projection.DecisionSolicitudPrestamo;
import co.pragma.model.solicitudprestamo.projection.SolicitudEvaluadaEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.math.BigDecimal;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcesarDecisionSolicitudUseCaseTest {

    @Mock
    private SolicitudPrestamoRepository solicitudPrestamoRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private SolicitudEvaluadaPublisher solicitudEvaluadaPublisher;

    @InjectMocks
    private ProcesarDecisionSolicitudUseCase useCase;

    private SolicitudPrestamo solicitudEnRevision;
    private DecisionSolicitudPrestamo decisionAprobada;
    private Cliente cliente;

    @BeforeEach
    void setUp() {
        solicitudEnRevision = SolicitudPrestamo.builder()
                .codigo("SP-1234")
                .idCliente(UUID.fromString("28f279f3-1ad7-47a7-a7e4-d3c9473afdc1"))
                .estado(EstadoSolicitudCodigo.REVISION_MANUAL)
                .monto(BigDecimal.valueOf(10000))
                .tasaInteres(BigDecimal.valueOf(0.05))
                .plazoEnMeses(12)
                .build();

        decisionAprobada = DecisionSolicitudPrestamo.builder()
                .codigoSolicitud("SP-1234")
                .decision(EstadoSolicitudCodigo.APROBADA)
                .build();

        cliente = Cliente.builder()
                .id(UUID.fromString("28f279f3-1ad7-47a7-a7e4-d3c9473afdc1"))
                .nombres("Juan")
                .apellidos("Perez")
                .build();
    }

    @Test
    void shouldUpdateSolicitudStateAndPublishEventSuccessfully() {
        when(solicitudPrestamoRepository.findByCodigo(decisionAprobada.getCodigoSolicitud())).thenReturn(Mono.just(solicitudEnRevision));
        when(solicitudPrestamoRepository.save(any(SolicitudPrestamo.class))).thenReturn(Mono.just(solicitudEnRevision)); // Mock the save operation
        when(clienteRepository.findById(solicitudEnRevision.getIdCliente())).thenReturn(Mono.just(cliente));
        when(solicitudEvaluadaPublisher.publish(any(SolicitudEvaluadaEvent.class))).thenReturn(Mono.empty());
        when(solicitudPrestamoRepository.markAsNotificado(anyString(), anyBoolean())).thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute(decisionAprobada))
                .assertNext(updatedSolicitud -> {
                    assertEquals(EstadoSolicitudCodigo.APROBADA, updatedSolicitud.getEstado());
                    assertEquals(false, updatedSolicitud.getNotificado());
                })
                .verifyComplete();

        verify(solicitudPrestamoRepository, times(1)).save(any(SolicitudPrestamo.class));
        verify(solicitudEvaluadaPublisher, times(1)).publish(any(SolicitudEvaluadaEvent.class));
    }

    @Test
    void shouldReturnErrorWhenSolicitudIsNotFound() {
        when(solicitudPrestamoRepository.findByCodigo(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute(decisionAprobada)).verifyError(SolicitudPrestamoNotFound.class);
    }

    @Test
    void shouldReturnErrorWhenSolicitudIsAlreadyProcessed() {
        solicitudEnRevision.setEstado(EstadoSolicitudCodigo.APROBADA);

        when(solicitudPrestamoRepository.findByCodigo(anyString())).thenReturn(Mono.just(solicitudEnRevision));

        StepVerifier.create(useCase.execute(decisionAprobada)).verifyError(SolicitudAlreadyProcessedException.class);
    }
}