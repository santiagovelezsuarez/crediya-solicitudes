package co.pragma.r2dbc;

import co.pragma.exception.ErrorCode;
import co.pragma.exception.InfrastructureException;
import co.pragma.model.estadosolicitud.EstadoSolicitud;
import co.pragma.model.estadosolicitud.EstadoSolicitudCodigo;
import co.pragma.r2dbc.adapter.EstadoSolicitudReactiveRepositoryAdapter;
import co.pragma.r2dbc.entity.EstadoSolicitudEntity;
import co.pragma.r2dbc.repository.EstadoSolicitudReactiveRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EstadoSolicitudRepositoryAdapterTest {

    @Mock
    private EstadoSolicitudReactiveRepository repository;

    @Mock
    private ObjectMapper mapper;

    @InjectMocks
    private EstadoSolicitudReactiveRepositoryAdapter adapter;

    private EstadoSolicitudEntity entity;
    private EstadoSolicitud domain;

    @BeforeEach
    void setUp() {
        entity = EstadoSolicitudEntity.builder()
                .id(1)
                .nombre(EstadoSolicitudCodigo.PENDIENTE_REVISION.name())
                .build();
        domain = EstadoSolicitud.builder()
                .id(1)
                .nombre(EstadoSolicitudCodigo.PENDIENTE_REVISION)
                .build();
    }

    @Test
    void findByIdShouldReturnMappedEstadoSolicitudWhenFound() {
        mockRepositoryFindById(entity);
        mockMapper(entity, domain);

        StepVerifier.create(adapter.findById((short) 1))
                .assertNext(result -> assertThat(result).isEqualTo(domain))
                .verifyComplete();
    }

    @Test
    void findByIdShouldReturnEmptyWhenNotFound() {
        when(repository.findById(anyShort())).thenReturn(Mono.empty());

        StepVerifier.create(adapter.findById((short) 99))
                .verifyComplete();
    }

    @Test
    void findByIdShouldReturnInfrastructureExceptionWhenRepositoryFails() {
        when(repository.findById(anyShort()))
                .thenReturn(Mono.error(new InfrastructureException("DB_ERROR", new RuntimeException("Connection failed"))));

        StepVerifier.create(adapter.findById((short) 1))
                .expectErrorMatches(throwable ->
                        throwable instanceof InfrastructureException &&
                        throwable.getMessage().equals(ErrorCode.DB_ERROR.name()) &&
                        throwable.getCause() instanceof RuntimeException)
                .verify();

        verify(repository).findById((short) 1);
    }

    @Test
    void findByNombreShouldReturnMappedEstadoSolicitudWhenFound() {
        mockRepositoryFindByNombre(entity);
        mockMapper(entity, domain);

        StepVerifier.create(adapter.findByNombre("PENDIENTE"))
                .assertNext(result -> assertThat(result).isEqualTo(domain))
                .verifyComplete();
    }

    @Test
    void findByNombreShouldReturnEmptyWhenNotFound() {
        when(repository.findByNombre(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(adapter.findByNombre("INEXISTENTE"))
                .verifyComplete();
    }

    @Test
    void findByNombreShouldReturnInfrastructureExceptionWhenRepositoryFails() {
        when(repository.findByNombre(anyString()))
                .thenReturn(Mono.error(new RuntimeException("DB connection failed")));

        StepVerifier.create(adapter.findByNombre("PENDIENTE"))
                .expectErrorMatches(throwable ->
                        throwable instanceof InfrastructureException &&
                        throwable.getMessage().equals(ErrorCode.DB_ERROR.name()) &&
                        throwable.getCause() instanceof RuntimeException)
                .verify();

        verify(repository).findByNombre("PENDIENTE");
    }

    private void mockRepositoryFindById(EstadoSolicitudEntity entity) {
        when(repository.findById(anyShort())).thenReturn(Mono.just(entity));
    }

    private void mockRepositoryFindByNombre(EstadoSolicitudEntity entity) {
        when(repository.findByNombre(anyString())).thenReturn(Mono.just(entity));
    }

    private void mockMapper(EstadoSolicitudEntity entity, EstadoSolicitud domain) {
        when(mapper.map(entity, EstadoSolicitud.class)).thenReturn(domain);
    }
}