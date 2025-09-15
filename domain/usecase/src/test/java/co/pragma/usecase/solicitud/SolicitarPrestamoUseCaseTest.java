package co.pragma.usecase.solicitud;

import co.pragma.exception.business.TipoPrestamoNotFoundException;
import co.pragma.model.solicitudprestamo.SolicitarPrestamoCommand;
import co.pragma.model.solicitudprestamo.SolicitudPrestamo;
import co.pragma.model.solicitudprestamo.gateways.SolicitudPrestamoRepository;
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

    @InjectMocks
    private SolicitarPrestamoUseCase useCase;

    private SolicitarPrestamoCommand cmd;
    private TipoPrestamo tipoPrestamo;
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
                .build();

        solicitudPrestamo = SolicitudPrestamo.builder()
                .idCliente(UUID.fromString(cmd.idCliente()))
                .monto(cmd.monto())
                .plazoEnMeses(cmd.plazoEnMeses())
                .idTipoPrestamo(tipoPrestamo.getId())
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
}
