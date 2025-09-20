package co.pragma.usecase.solicitud;

import co.pragma.exception.business.TipoPrestamoNotFoundException;
import co.pragma.model.solicitudprestamo.command.SolicitarPrestamoCommand;
import co.pragma.model.solicitudprestamo.SolicitudPrestamo;
import co.pragma.model.solicitudprestamo.gateways.SolicitudPrestamoRepository;
import co.pragma.model.solicitudprestamo.gateways.ValidacionAutomaticaEventPublisher;
import co.pragma.model.tipoprestamo.TipoPrestamo;
import co.pragma.model.tipoprestamo.gateways.TipoPrestamoRepository;
import co.pragma.usecase.solicitud.businessrules.TipoPrestamoValidator;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SolicitarPrestamoUseCaseTest {

    @Mock
    private SolicitudPrestamoRepository solicitudPrestamoRepository;

    @Mock
    private TipoPrestamoRepository tipoPrestamoRepository;

    @Mock
    private TipoPrestamoValidator tipoPrestamoValidator;

    @Mock
    private ValidacionAutomaticaEventPublisher validacionAutomaticaEventPublisher;

    @InjectMocks
    private SolicitarPrestamoUseCase useCase;

    private SolicitarPrestamoCommand cmd;
    private TipoPrestamo tipoPrestamo;
    private TipoPrestamo tipoPrestamoManual;
    private TipoPrestamo tipoPrestamoAutomatico;
    private SolicitudPrestamo solicitudPrestamo;

    @BeforeEach
    void setUp() {
        cmd = SolicitarPrestamoCommand.builder()
                .idCliente(UUID.randomUUID().toString())
                .monto(new BigDecimal("10000.00"))
                .plazoEnMeses(12)
                .tipoPrestamo("HIPOTECARIO")
                .build();

        tipoPrestamo = TipoPrestamo.builder()
                .id(UUID.randomUUID())
                .nombre("HIPOTECARIO")
                .validacionAutomatica(false)
                .build();

        tipoPrestamoAutomatico = TipoPrestamo.builder()
                .id(UUID.randomUUID())
                .nombre("AUTOMATICA")
                .validacionAutomatica(true)
                .tasaInteres(new BigDecimal("0.08"))
                .build();

        solicitudPrestamo = SolicitudPrestamo.builder()
                .idCliente(UUID.fromString(cmd.idCliente()))
                .monto(cmd.monto())
                .plazoEnMeses(cmd.plazoEnMeses())
                .idTipoPrestamo(tipoPrestamo.getId())
                .build();

        tipoPrestamoManual = TipoPrestamo.builder()
                .id(UUID.randomUUID())
                .nombre("MANUAL")
                .validacionAutomatica(false)
                .tasaInteres(new BigDecimal("0.10"))
                .build();
    }

    @Test
    void shouldSaveSolicitudPrestamoWhenCommandIsValid() {
        when(tipoPrestamoRepository.findByNombre(cmd.tipoPrestamo())).thenReturn(Mono.just(tipoPrestamo));
        when(tipoPrestamoValidator.validate(cmd)).thenReturn(Mono.empty());
        when(solicitudPrestamoRepository.save(any(SolicitudPrestamo.class))).thenReturn(Mono.just(solicitudPrestamo));

        StepVerifier.create(useCase.execute(cmd))
                .expectNextMatches(solicitud -> solicitud.getIdCliente().equals(solicitudPrestamo.getIdCliente()) &&
                        solicitud.getMonto().equals(solicitudPrestamo.getMonto()))
                .verifyComplete();

        verify(tipoPrestamoRepository).findByNombre(cmd.tipoPrestamo());
        verify(tipoPrestamoValidator).validate(cmd);
        verify(solicitudPrestamoRepository).save(any(SolicitudPrestamo.class));
    }

    @Test
    void shouldReturnErrorWhenTipoPrestamoNotFound() {
        when(tipoPrestamoRepository.findByNombre(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute(cmd))
                .expectError(TipoPrestamoNotFoundException.class)
                .verify();

        verify(tipoPrestamoRepository).findByNombre(cmd.tipoPrestamo());
        verifyNoInteractions(tipoPrestamoValidator);
        verifyNoInteractions(solicitudPrestamoRepository);
    }

    @Test
    void shouldReturnErrorWhenValidatorFails() {
        when(tipoPrestamoRepository.findByNombre(anyString())).thenReturn(Mono.just(tipoPrestamoManual));
        when(tipoPrestamoValidator.validate(any())).thenReturn(Mono.error(new IllegalArgumentException("Validation failed")));

        StepVerifier.create(useCase.execute(cmd))
                .expectError(IllegalArgumentException.class)
                .verify();

        verify(solicitudPrestamoRepository, never()).save(any(SolicitudPrestamo.class));
        verifyNoInteractions(validacionAutomaticaEventPublisher);
    }
}
