package co.pragma.r2dbc;

import co.pragma.error.ErrorCode;
import co.pragma.exception.InfrastructureException;
import co.pragma.model.tipoprestamo.TipoPrestamo;
import co.pragma.model.tipoprestamo.TipoPrestamoInfo;
import co.pragma.r2dbc.adapter.TipoPrestamoReactiveRepositoryAdapter;
import co.pragma.r2dbc.entity.TipoPrestamoEntity;
import co.pragma.r2dbc.repository.TipoPrestamoReactiveRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TipoPrestamoRepositoryAdapterTest {

    @InjectMocks
    private TipoPrestamoReactiveRepositoryAdapter repositoryAdapter;

    @Mock
    private TipoPrestamoReactiveRepository reactiveRepository;

    @Mock
    private ObjectMapper mapper;

    private TipoPrestamoEntity entity;
    private TipoPrestamo model;
    private TipoPrestamoInfo info;
    private UUID uuid;

    @BeforeEach
    void setUp() {
        uuid = UUID.randomUUID();
        entity = createEntity();
        model = createModel();
        info = createInfo();
    }

    @Test
    void shouldFindByNombre() {
        mockFindByNombre();

        StepVerifier.create(repositoryAdapter.findByNombre("EDUCACION"))
                .expectNextMatches(tp -> tp.getNombre().equals("EDUCACION"))
                .verifyComplete();

        verifyMocksForFindByNombre();
    }

    @Test
    void shouldFindByNombreReturnInfrastructureExceptionWhenRepositoryFails() {
        when(reactiveRepository.findByNombre(any())).thenReturn(Mono.error(new RuntimeException("DB error")));

        StepVerifier.create(repositoryAdapter.findByNombre("Personal"))
                .expectErrorMatches(this::isInfrastructureException)
                .verify();
    }

    @Test
    void shouldFindById() {
        mockFindById();

        StepVerifier.create(repositoryAdapter.findById(uuid.toString()))
                .expectNextMatches(tp -> tp.getId().equals(uuid))
                .verifyComplete();
    }

    @Test
    void shouldFindByIdReturnInfrastructureExceptionWhenRepositoryFails() {
        when(reactiveRepository.findById(any(UUID.class))).thenReturn(Mono.error(new RuntimeException("DB error")));

        StepVerifier.create(repositoryAdapter.findById(uuid.toString()))
                .expectErrorMatches(this::isInfrastructureException)
                .verify();
    }

    @Test
    void shouldFindByIdIn() {
        mockFindByIdIn();

        StepVerifier.create(repositoryAdapter.findByIdIn(List.of(uuid)))
                .expectNextMatches(info -> info.getId().equals(uuid))
                .verifyComplete();
    }

    @Test
    void shouldFindByIdInReturnEmptyFluxWhenNotFound() {
        when(reactiveRepository.findByIdIn(anyList())).thenReturn(Flux.empty());

        StepVerifier.create(repositoryAdapter.findByIdIn(List.of(UUID.randomUUID())))
                .verifyComplete();
    }

    @Test
    void shouldFindByIdInReturnInfrastructureExceptionWhenRepositoryFails() {
        when(reactiveRepository.findByIdIn(anyList())).thenReturn(Flux.error(new RuntimeException("DB error")));

        StepVerifier.create(repositoryAdapter.findByIdIn(List.of(uuid)))
                .expectErrorMatches(this::isInfrastructureException)
                .verify();
    }

    private TipoPrestamoEntity createEntity() {
        return TipoPrestamoEntity.builder()
                .id(uuid)
                .nombre("EDUCACION")
                .montoMinimo(BigDecimal.valueOf(100000))
                .montoMaximo(BigDecimal.valueOf(10000000))
                .tasaInteres(BigDecimal.valueOf(12.87))
                .validacionAutomatica(true)
                .build();
    }

    private TipoPrestamo createModel() {
        return TipoPrestamo.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .montoMinimo(entity.getMontoMinimo())
                .montoMaximo(entity.getMontoMaximo())
                .tasaInteres(entity.getTasaInteres())
                .validacionAutomatica(entity.isValidacionAutomatica())
                .build();
    }

    private TipoPrestamoInfo createInfo() {
        return TipoPrestamoInfo.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .build();
    }

    private void mockFindByNombre() {
        when(reactiveRepository.findByNombre(any())).thenReturn(Mono.just(entity));
        when(mapper.map(entity, TipoPrestamo.class)).thenReturn(model);
    }

    private void verifyMocksForFindByNombre() {
        verify(reactiveRepository).findByNombre(any());
        verify(mapper).map(entity, TipoPrestamo.class);
    }

    private void mockFindById() {
        when(reactiveRepository.findById(any(UUID.class))).thenReturn(Mono.just(entity));
        when(mapper.map(entity, TipoPrestamo.class)).thenReturn(model);
    }

    private void mockFindByIdIn() {
        when(reactiveRepository.findByIdIn(anyList())).thenReturn(Flux.just(entity));
        when(mapper.map(entity, TipoPrestamoInfo.class)).thenReturn(info);
    }

    private boolean isInfrastructureException(Throwable throwable) {
        return throwable instanceof InfrastructureException &&
                throwable.getMessage().equals(ErrorCode.DB_ERROR.name()) &&
                throwable.getCause() instanceof RuntimeException;
    }
}
