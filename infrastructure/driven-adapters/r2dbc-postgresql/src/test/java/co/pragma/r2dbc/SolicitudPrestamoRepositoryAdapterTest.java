package co.pragma.r2dbc;

import co.pragma.error.ErrorCode;
import co.pragma.exception.InfrastructureException;
import co.pragma.model.solicitudprestamo.SolicitudPrestamo;
import co.pragma.r2dbc.adapter.SolicitudPrestamoReactiveRepositoryAdapter;
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

    @InjectMocks
    private SolicitudPrestamoReactiveRepositoryAdapter repositoryAdapter;

    @Mock
    private SolicitudPrestamoReactiveRepository repository;

    @Mock
    private SolicitudPrestamoEntityMapper mapper;

    private SolicitudPrestamo solicitudPrestamo;
    private SolicitudPrestamoEntity entity;

    @BeforeEach
    void setUp() {
        UUID id = UUID.randomUUID();
        solicitudPrestamo = createSolicitudPrestamo(id);
        entity = createSolicitudPrestamoEntity(id);
    }

    @Test
    void shouldSaveSolicitudPrestamoSuccessfully() {
        mockSave();

        StepVerifier.create(repositoryAdapter.save(solicitudPrestamo))
                .expectNextMatches(saved -> saved.getId().equals(solicitudPrestamo.getId()))
                .verifyComplete();

        verify(repository).save(any(SolicitudPrestamoEntity.class));
    }

    @Test
    void shouldReturnInfrastructureExceptionWhenSaveFails() {
        mockSaveFailure();

        StepVerifier.create(repositoryAdapter.save(solicitudPrestamo))
                .expectErrorMatches(this::isInfrastructureException)
                .verify();
    }

    @Test
    void shouldFindByIdEstadoInSuccessfully() {
        mockFindByIdEstadoIn();

        StepVerifier.create(repositoryAdapter.findByIdEstadoIn(List.of(1, 2), 0, 10))
                .expectNextMatches(found -> found.getId().equals(solicitudPrestamo.getId()))
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyFluxWhenNoSolicitudesFoundForGivenStates() {
        when(repository.findByIdEstadoIn(anyList(), anyInt(), anyInt())).thenReturn(Flux.empty());

        StepVerifier.create(repositoryAdapter.findByIdEstadoIn(List.of(1), 0, 10))
                .verifyComplete();
    }

    @Test
    void shouldReturnInfrastructureExceptionWhenFindByIdEstadoInFails() {
        when(repository.findByIdEstadoIn(anyList(), anyInt(), anyInt()))
                .thenReturn(Flux.error(new RuntimeException("DB error")));

        StepVerifier.create(repositoryAdapter.findByIdEstadoIn(List.of(1), 0, 10))
                .expectErrorMatches(this::isInfrastructureException)
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

    private void mockSave() {
        when(repository.save(any(SolicitudPrestamoEntity.class))).thenReturn(Mono.just(entity));
        when(mapper.toEntity(any(SolicitudPrestamo.class))).thenReturn(entity);
        when(mapper.toDomain(any(SolicitudPrestamoEntity.class))).thenReturn(solicitudPrestamo);
    }

    private void mockSaveFailure() {
        when(mapper.toEntity(any(SolicitudPrestamo.class))).thenReturn(entity);
        when(repository.save(any(SolicitudPrestamoEntity.class)))
                .thenReturn(Mono.error(new RuntimeException("DB error")));
    }

    private void mockFindByIdEstadoIn() {
        when(repository.findByIdEstadoIn(anyList(), anyInt(), anyInt())).thenReturn(Flux.just(entity));
        when(mapper.toDomain(any(SolicitudPrestamoEntity.class))).thenReturn(solicitudPrestamo);
    }

    private boolean isInfrastructureException(Throwable throwable) {
        return throwable instanceof InfrastructureException &&
                throwable.getMessage().equals(ErrorCode.DB_ERROR.name()) &&
                throwable.getCause() instanceof RuntimeException;
    }
}
