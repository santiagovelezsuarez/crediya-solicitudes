package co.pragma.r2dbc.adapter;

import co.pragma.exception.ErrorCode;
import co.pragma.exception.InfrastructureException;
import co.pragma.model.estadosolicitud.EstadoSolicitudCodigo;
import co.pragma.model.solicitudprestamo.SolicitudPrestamo;
import co.pragma.r2dbc.entity.SolicitudPrestamoEntity;
import co.pragma.r2dbc.mapper.SolicitudPrestamoEntityMapper;
import co.pragma.r2dbc.repository.SolicitudPrestamoReactiveRepository;
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
import java.util.List;
import java.util.UUID;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SolicitudPrestamoRepositoryAdapterTest {

    @Mock
    private SolicitudPrestamoReactiveRepository repository;

    @Mock
    private SolicitudPrestamoEntityMapper mapper;

    @InjectMocks
    private SolicitudPrestamoReactiveRepositoryAdapter adapter;

    private SolicitudPrestamo solicitudPrestamo;
    private SolicitudPrestamoEntity entity;
    private UUID idCliente;

    @BeforeEach
    void setUp() {
        idCliente = UUID.randomUUID();
        solicitudPrestamo = createSolicitudPrestamo(idCliente);
        entity = createSolicitudPrestamoEntity(idCliente);
    }

    @Test
    void shouldSaveSolicitudPrestamoSuccessfully() {
        when(mapper.toEntity(any(SolicitudPrestamo.class))).thenReturn(entity);
        when(repository.save(entity)).thenReturn(Mono.just(entity));
        when(mapper.toDomain(any(SolicitudPrestamoEntity.class))).thenReturn(solicitudPrestamo);

        StepVerifier.create(adapter.save(solicitudPrestamo))
                .expectNextMatches(saved -> saved.getId().equals(solicitudPrestamo.getId()))
                .verifyComplete();
    }

    @Test
    void shouldFindByIdEstadoInSuccessfully() {
        List<Integer> estados = List.of(1, 2);

        when(repository.findByIdEstadoIn(estados, 0, 0)).thenReturn(Flux.just(entity));
        when(mapper.toDomain(entity)).thenReturn(solicitudPrestamo);

        StepVerifier.create(adapter.findByIdEstadoIn(estados, 0, 0))
                .expectNext(solicitudPrestamo)
                .verifyComplete();
    }

    @Test
    void shouldCalculateOffsetCorrectlyForFindByIdEstadoIn() {
        List<Integer> estados = List.of(1);
        int page = 2;
        int size = 5;
        int expectedOffset = 10;

        when(repository.findByIdEstadoIn(estados, size, expectedOffset)).thenReturn(Flux.empty());

        adapter.findByIdEstadoIn(estados, page, size).subscribe();

        verify(repository).findByIdEstadoIn(estados, size, expectedOffset);
    }

    @Test
    void shouldReturnEmptyFluxWhenNoSolicitudesFoundForGivenStates() {
        when(repository.findByIdEstadoIn(anyList(), anyInt(), anyInt())).thenReturn(Flux.empty());

        StepVerifier.create(adapter.findByIdEstadoIn(List.of(1), 0, 10))
                .verifyComplete();
    }

    @Test
    void shouldReturnInfrastructureExceptionWhenFindByIdEstadoInFails() {
        when(repository.findByIdEstadoIn(anyList(), anyInt(), anyInt()))
                .thenReturn(Flux.error(new RuntimeException("DB error")));

        StepVerifier.create(adapter.findByIdEstadoIn(List.of(1), 0, 10))
                .expectErrorMatches(this::isInfrastructureException)
                .verify();
    }

    @Test
    void shouldReturnEmptyFluxWhenNoSolicitudesFoundByIdClienteAndIdEstado() {
        when(repository.findByIdClienteAndIdEstado(any(UUID.class), anyInt()))
                .thenReturn(Flux.empty());

        StepVerifier.create(adapter.findByIdClienteAndIdEstado(UUID.randomUUID(), EstadoSolicitudCodigo.RECHAZADA))
                .verifyComplete();
    }

    @Test
    void shouldReturnInfrastructureExceptionWhenFindByIdClienteAndIdEstadoFails() {
        when(repository.findByIdClienteAndIdEstado(any(UUID.class), anyInt()))
                .thenReturn(Flux.error(new RuntimeException("DB error")));

        StepVerifier.create(adapter.findByIdClienteAndIdEstado(idCliente, EstadoSolicitudCodigo.PENDIENTE_REVISION))
                .expectError(InfrastructureException.class)
                .verify();
    }

    private SolicitudPrestamo createSolicitudPrestamo(UUID id) {
        return SolicitudPrestamo.builder()
                .id(id)
                .idCliente(UUID.randomUUID())
                .idTipoPrestamo(UUID.randomUUID())
                .monto(new BigDecimal("5000000"))
                .plazoEnMeses(12)
                .build();
    }

    private SolicitudPrestamoEntity createSolicitudPrestamoEntity(UUID id) {
        return SolicitudPrestamoEntity.builder()
                .id(id)
                .idCliente(solicitudPrestamo.getIdCliente())
                .idTipoPrestamo(solicitudPrestamo.getIdTipoPrestamo())
                .monto(solicitudPrestamo.getMonto())
                .plazoEnMeses(solicitudPrestamo.getPlazoEnMeses())
                .build();
    }

    private boolean isInfrastructureException(Throwable throwable) {
        return throwable instanceof InfrastructureException &&
                throwable.getMessage().equals(ErrorCode.DB_ERROR.name()) &&
                throwable.getCause() instanceof RuntimeException;
    }
}
