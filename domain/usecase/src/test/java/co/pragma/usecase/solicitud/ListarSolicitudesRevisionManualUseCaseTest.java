package co.pragma.usecase.solicitud;

import co.pragma.model.cliente.ClienteInfo;
import co.pragma.model.cliente.gateways.UsuarioPort;
import co.pragma.model.solicitudprestamo.SolicitudPrestamo;
import co.pragma.model.solicitudprestamo.gateways.SolicitudPrestamoRepository;
import co.pragma.model.tipoprestamo.TipoPrestamoInfo;
import co.pragma.model.tipoprestamo.gateways.TipoPrestamoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ListarSolicitudesRevisionManualUseCaseTest {

    @Mock
    private SolicitudPrestamoRepository solicitudPrestamoRepository;

    @Mock
    private TipoPrestamoRepository tipoPrestamoRepository;

    @Mock
    private UsuarioPort usuarioPort;

    @InjectMocks
    private ListarSolicitudesRevisionManualUseCase useCase;

    private List<SolicitudPrestamo> solicitudes;
    private List<TipoPrestamoInfo> tipos;
    private List<ClienteInfo> clientes;

    @BeforeEach
    void setUp() {
        solicitudes = IntStream.range(0, 2)
                .mapToObj(i -> SolicitudPrestamo.builder()
                        .id(UUID.fromString(UUID.randomUUID().toString()))
                        .idCliente(UUID.randomUUID())
                        .idTipoPrestamo(UUID.randomUUID())
                        .monto(new BigDecimal("10000.00"))
                        .plazoEnMeses(12)
                        .estado(co.pragma.model.estadosolicitud.EstadoSolicitudCodigo.PENDIENTE_REVISION)
                        .build())
                .collect(Collectors.toList());

        tipos = solicitudes.stream()
                .map(s -> TipoPrestamoInfo.builder().id(s.getIdTipoPrestamo()).nombre("Personal").tasaInteres(new BigDecimal("0.10")).build())
                .collect(Collectors.toList());

        clientes = solicitudes.stream()
                .map(s -> new ClienteInfo(s.getIdCliente(), "test" + s.getId() + "@mail.com", "Test User", new BigDecimal("5000"), new BigDecimal("100")))
                .collect(Collectors.toList());
    }

    @Test
    void shouldReturnResolvedListWhenDataIsAvailable() {
        when(solicitudPrestamoRepository.findByIdEstadoIn(any(), anyInt(), anyInt())).thenReturn(Flux.fromIterable(solicitudes));
        when(usuarioPort.getClientesByIdIn(any())).thenReturn(Flux.fromIterable(clientes));
        when(tipoPrestamoRepository.findByIdIn(any())).thenReturn(Flux.fromIterable(tipos));

        StepVerifier.create(useCase.execute(0, 10))
                .expectNextMatches(resultList -> {
                    return !resultList.isEmpty() && resultList.size() == solicitudes.size();
                })
                .verifyComplete();

        verify(solicitudPrestamoRepository).findByIdEstadoIn(any(), eq(0), eq(10));
        verify(usuarioPort).getClientesByIdIn(any());
        verify(tipoPrestamoRepository).findByIdIn(any());
    }

    @Test
    void shouldReturnEmptyListWhenNoSolicitudesFound() {
        when(solicitudPrestamoRepository.findByIdEstadoIn(any(), anyInt(), anyInt())).thenReturn(Flux.empty());

        StepVerifier.create(useCase.execute(0, 10))
                .expectNextMatches(List::isEmpty)
                .verifyComplete();

        verify(solicitudPrestamoRepository).findByIdEstadoIn(any(), eq(0), eq(10));
        verifyNoInteractions(usuarioPort);
        verifyNoInteractions(tipoPrestamoRepository);
    }
}
