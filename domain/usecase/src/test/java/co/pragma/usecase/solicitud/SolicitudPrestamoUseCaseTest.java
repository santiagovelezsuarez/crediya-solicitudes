package co.pragma.usecase.solicitud;

import co.pragma.exception.ClienteNotFoundException;
import co.pragma.model.cliente.Cliente;
import co.pragma.model.cliente.DocumentoIdentidadVO;
import co.pragma.model.estadosolicitud.EstadoSolicitudCodigo;
import co.pragma.model.solicitudprestamo.SolicitudPrestamo;
import co.pragma.model.solicitudprestamo.gateways.SolicitudPrestamoRepository;
import co.pragma.model.tipoprestamo.TipoPrestamo;
import co.pragma.model.tipoprestamo.TipoPrestamoVO;
import co.pragma.usecase.solicitud.businessrules.ClienteValidator;
import co.pragma.usecase.solicitud.businessrules.TipoPrestamoValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.math.BigDecimal;
import java.util.UUID;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SolicitudPrestamoUseCaseTest {

    @Mock
    private SolicitudPrestamoRepository solicitudPrestamoRepository;

    @Mock
    private TipoPrestamoValidator tipoPrestamoValidator;

    @Mock
    private ClienteValidator clienteValidator;

    private SolicitudPrestamoUseCase solicitudPrestamoUseCase;

    @BeforeEach
    void setUp() {
        solicitudPrestamoUseCase = new SolicitudPrestamoUseCase(
                solicitudPrestamoRepository,
                tipoPrestamoValidator,
                clienteValidator
        );
    }

    @Test
    void shouldCreateSolicitudPrestamoSuccessfully() {
        var solicitud = SolicitudPrestamo.builder()
                .monto(BigDecimal.valueOf(10000))
                .plazoEnMeses(12)
                .build();

        var cliente = Cliente.builder()
                .id(UUID.randomUUID())
                .email("test@mail.com")
                .build();

        var tipoPrestamo = TipoPrestamo.builder()
                .id(UUID.randomUUID())
                .nombre("LIBRE_INVERSION")
                .build();

        when(clienteValidator.validate(any(DocumentoIdentidadVO.class)))
                .thenReturn(Mono.just(cliente));

        when(tipoPrestamoValidator.validate(any(TipoPrestamoVO.class)))
                .thenReturn(Mono.just(tipoPrestamo));

        when(solicitudPrestamoRepository.save(any(SolicitudPrestamo.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        var result = solicitudPrestamoUseCase.createSolicitud(
                solicitud,
                new DocumentoIdentidadVO("CC", "123456789"),
                new TipoPrestamoVO("LIBRE_INVERSION")
        );

        StepVerifier.create(result)
                .expectNextMatches(saved ->
                        saved.getIdCliente().equals(cliente.getId()) &&
                                saved.getIdTipoPrestamo().equals(tipoPrestamo.getId()) &&
                                saved.getEstado().equals(EstadoSolicitudCodigo.PENDIENTE_REVISION)
                )
                .verifyComplete();

        verify(solicitudPrestamoRepository, times(1)).save(any(SolicitudPrestamo.class));
    }

    @Test
    void createSolicitudShouldFailsWhenClienteNotFound() {
        when(clienteValidator.validate(any(DocumentoIdentidadVO.class)))
                .thenReturn(Mono.error(new ClienteNotFoundException("Cliente no encontrado")));

        when(tipoPrestamoValidator.validate(any(TipoPrestamoVO.class)))
                .thenReturn(Mono.just(
                        TipoPrestamo.builder()
                                .id(UUID.randomUUID())
                                .nombre("LIBRE_INVERSION")
                                .build()
                ));

        var solicitud = SolicitudPrestamo.builder().build();

        var result = solicitudPrestamoUseCase.createSolicitud(
                solicitud,
                new DocumentoIdentidadVO("CC", "0000"),
                new TipoPrestamoVO("LIBRE_INVERSION")
        );

        StepVerifier.create(result)
                .expectErrorMatches(e -> e.getMessage().equals("Cliente no encontrado"))
                .verify();

        verify(solicitudPrestamoRepository, never()).save(any());
    }

    @Test
    void createSolicitudShouldFailsWhenTipoPrestamoNotFound() {
        when(clienteValidator.validate(any(DocumentoIdentidadVO.class)))
                .thenReturn(Mono.just(
                        Cliente.builder()
                                .id(UUID.randomUUID())
                                .email("pepe@mail.co")
                                .build()
                ));

        when(tipoPrestamoValidator.validate(any(TipoPrestamoVO.class)))
                .thenReturn(Mono.error(new RuntimeException("Tipo de prestamo no encontrado")));

        var solicitud = SolicitudPrestamo.builder().build();

        var result = solicitudPrestamoUseCase.createSolicitud(
                solicitud,
                new DocumentoIdentidadVO("CC", "123456789"),
                new TipoPrestamoVO("NO_EXISTE")
        );

        StepVerifier.create(result)
                .expectErrorMatches(e -> e.getMessage().equals("Tipo de prestamo no encontrado"))
                .verify();

        verify(solicitudPrestamoRepository, never()).save(any());
    }
}
