package co.pragma.usecase.solicitud;

import co.pragma.exception.business.TipoPrestamoNotFoundException;
import co.pragma.model.cliente.Cliente;
import co.pragma.model.cliente.gateways.ClienteRepository;
import co.pragma.model.estadosolicitud.EstadoSolicitudCodigoEnum;
import co.pragma.model.solicitudprestamo.SolicitudPrestamo;
import co.pragma.model.solicitudprestamo.command.SolicitarPrestamoCommand;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SolicitarPrestamoUseCaseTest {

    @Mock
    private SolicitudPrestamoRepository solicitudRepo;
    @Mock
    private TipoPrestamoRepository tipoRepo;
    @Mock
    private TipoPrestamoValidator validator;
    @Mock
    private ValidacionAutomaticaEventPublisher publisher;
    @Mock
    private ClienteRepository clienteRepo;

    @InjectMocks
    private SolicitarPrestamoUseCase useCase;

    private SolicitarPrestamoCommand cmdManual;
    private SolicitarPrestamoCommand cmdAutomatico;
    private SolicitarPrestamoCommand cmdHipotecario;
    private TipoPrestamo tipoManual;
    private TipoPrestamo tipoAutomatico;
    private TipoPrestamo tipoHipotecario;
    private SolicitudPrestamo solicitud;
    private Cliente cliente;

    @BeforeEach
    void setUp() {
        UUID clienteId = UUID.randomUUID();
        cmdManual = new SolicitarPrestamoCommand(clienteId.toString(), BigDecimal.valueOf(10000), 12, "MANUAL");
        cmdAutomatico = new SolicitarPrestamoCommand(clienteId.toString(), BigDecimal.valueOf(5000), 6, "AUTOMATICO");
        cmdHipotecario = new SolicitarPrestamoCommand(UUID.randomUUID().toString(), BigDecimal.valueOf(10000), 12, "HIPOTECARIO");

        tipoManual = TipoPrestamo.builder().id(UUID.randomUUID()).nombre("MANUAL").validacionAutomatica(false).tasaInteres(BigDecimal.valueOf(0.1)).build();
        tipoAutomatico = TipoPrestamo.builder().id(UUID.randomUUID()).nombre("AUTOMATICO").validacionAutomatica(true).tasaInteres(BigDecimal.valueOf(0.08)).build();
        tipoHipotecario = TipoPrestamo.builder().id(UUID.randomUUID()).nombre("HIPOTECARIO").validacionAutomatica(false).build();

        solicitud = SolicitudPrestamo.builder()
                .id(UUID.randomUUID())
                .idCliente(UUID.fromString(cmdHipotecario.idCliente()))
                .monto(cmdHipotecario.monto())
                .plazoEnMeses(cmdHipotecario.plazoEnMeses())
                .idTipoPrestamo(tipoHipotecario.getId())
                .estado(EstadoSolicitudCodigoEnum.PENDIENTE_VALIDACION_AUTOMATICA)
                .build();

        cliente = Cliente.builder().id(clienteId).nombres("santi").apellidos("velez").email("santi@mail.co").build();
    }

    private void mockTipoPrestamo(String nombre, TipoPrestamo tipo) {
        when(tipoRepo.findByNombre(nombre)).thenReturn(Mono.just(tipo));
        when(validator.validate(any())).thenReturn(Mono.empty());
    }

    @Test
    void shouldSaveSolicitudPrestamoWhenCommandIsValid() {
        mockTipoPrestamo(cmdHipotecario.tipoPrestamo(), tipoHipotecario);
        when(solicitudRepo.save(any())).thenReturn(Mono.just(solicitud));

        StepVerifier.create(useCase.execute(cmdHipotecario))
                .expectNextMatches(s -> s.getMonto().equals(solicitud.getMonto()))
                .verifyComplete();

        verify(solicitudRepo).save(any());
    }

    @Test
    void shouldReturnErrorWhenTipoPrestamoNotFound() {
        when(tipoRepo.findByNombre(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute(cmdHipotecario))
                .expectError(TipoPrestamoNotFoundException.class)
                .verify();

        verifyNoInteractions(solicitudRepo);
    }

    @Test
    void shouldReturnErrorWhenValidatorFails() {
        mockTipoPrestamo(cmdManual.tipoPrestamo(), tipoManual);
        when(validator.validate(any())).thenReturn(Mono.error(new IllegalArgumentException("Validation failed")));

        StepVerifier.create(useCase.execute(cmdManual))
                .expectError(IllegalArgumentException.class)
                .verify();

        verify(solicitudRepo, never()).save(any());
    }

    @Test
    void shouldSaveAndNotPublishEventForManualLoan() {
        mockTipoPrestamo(cmdManual.tipoPrestamo(), tipoManual);
        when(solicitudRepo.save(any())).thenAnswer(inv -> {
            SolicitudPrestamo s = inv.getArgument(0);
            s.setId(UUID.randomUUID());
            return Mono.just(s);
        });

        StepVerifier.create(useCase.execute(cmdManual))
                .expectNextMatches(s -> s.getEstado().equals(EstadoSolicitudCodigoEnum.PENDIENTE_REVISION))
                .verifyComplete();

        verifyNoInteractions(publisher);
    }

    @Test
    void shouldSaveAndPublishEventForAutomaticLoan() {
        mockTipoPrestamo(cmdAutomatico.tipoPrestamo(), tipoAutomatico);
        when(solicitudRepo.save(any())).thenReturn(Mono.just(solicitud));
        when(clienteRepo.findById(any())).thenReturn(Mono.just(cliente));
        when(tipoRepo.findById(any())).thenReturn(Mono.just(tipoAutomatico));
        when(solicitudRepo.findByIdClienteAndIdEstado(any(), any())).thenReturn(Flux.empty());
        when(publisher.publish(any())).thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute(cmdAutomatico))
                .expectNextMatches(s -> s.getEstado().equals(EstadoSolicitudCodigoEnum.PENDIENTE_VALIDACION_AUTOMATICA))
                .verifyComplete();

        verify(publisher).publish(any());
    }
}
