package co.pragma.r2dbc.adapter;

import co.pragma.exception.ErrorCode;
import co.pragma.exception.InfrastructureException;
import co.pragma.model.cliente.Cliente;
import co.pragma.r2dbc.entity.ClienteEntity;
import co.pragma.r2dbc.repository.ClienteReactiveRepository;
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

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClienteReactiveRepositoryAdapterTest {

    @Mock
    private ClienteReactiveRepository repository;

    @Mock
    private ObjectMapper mapper;

    @InjectMocks
    private ClienteReactiveRepositoryAdapter adapter;

    private Cliente cliente;
    private ClienteEntity clienteEntity;
    private UUID clienteId;

    @BeforeEach
    void setUp() {
        clienteId = UUID.randomUUID();
        clienteEntity = ClienteEntity.builder()
                .id(clienteId)
                .nombres("John")
                .apellidos("Doe")
                .build();

        cliente = Cliente.builder()
                .id(clienteId)
                .nombres("John")
                .apellidos("Doe")
                .build();
    }

    @Test
    void shouldReturnClienteWhenIdExists() {
        when(repository.findById(clienteId)).thenReturn(Mono.just(clienteEntity));
        when(mapper.map(clienteEntity, Cliente.class)).thenReturn(cliente);

        StepVerifier.create(adapter.findById(clienteId))
                .expectNextMatches(result -> result.getId().equals(clienteId) && result.getNombres().equals("John"))
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyWhenIdDoesNotExist() {
        when(repository.findById(any(UUID.class))).thenReturn(Mono.empty());

        StepVerifier.create(adapter.findById(UUID.randomUUID()))
                .verifyComplete();
    }

    @Test
    void shouldWrapErrorInInfrastructureExceptionForFindById() {
        when(repository.findById(any(UUID.class))).thenReturn(Mono.error(new RuntimeException("DB error")));

        StepVerifier.create(adapter.findById(UUID.randomUUID()))
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(InfrastructureException.class);
                    assertThat(error.getMessage()).isEqualTo(ErrorCode.DB_ERROR.name());
                })
                .verify();
    }

    @Test
    void shouldReturnClientesWhenIdsExist() {
        UUID clienteId2 = UUID.randomUUID();
        ClienteEntity clienteEntity2 = ClienteEntity.builder().id(clienteId2).nombres("Jane").build();
        Cliente cliente2 = Cliente.builder().id(clienteId2).nombres("Jane").build();
        List<UUID> ids = List.of(clienteId, clienteId2);

        when(repository.findByIdIn(ids)).thenReturn(Flux.just(clienteEntity, clienteEntity2));
        when(mapper.map(clienteEntity, Cliente.class)).thenReturn(cliente);
        when(mapper.map(clienteEntity2, Cliente.class)).thenReturn(cliente2);

        StepVerifier.create(adapter.findByIdIn(ids))
                .expectNext(cliente)
                .expectNext(cliente2)
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyFluxWhenNoIdsMatch() {
        when(repository.findByIdIn(anyList())).thenReturn(Flux.empty());

        StepVerifier.create(adapter.findByIdIn(List.of(UUID.randomUUID())))
                .verifyComplete();
    }

    @Test
    void shouldReturnMatchingClientesWhenSomeIdsExist() {
        UUID nonExistentId = UUID.randomUUID();
        List<UUID> ids = List.of(clienteId, nonExistentId);

        when(repository.findByIdIn(ids)).thenReturn(Flux.just(clienteEntity));
        when(mapper.map(clienteEntity, Cliente.class)).thenReturn(cliente);

        StepVerifier.create(adapter.findByIdIn(ids))
                .expectNext(cliente)
                .verifyComplete();
    }

    @Test
    void shouldWrapErrorInInfrastructureExceptionForFindByIdIn() {
        when(repository.findByIdIn(anyList())).thenReturn(Flux.error(new RuntimeException("DB error")));

        StepVerifier.create(adapter.findByIdIn(List.of(UUID.randomUUID())))
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(InfrastructureException.class);
                    assertThat(error.getMessage()).isEqualTo(ErrorCode.DB_ERROR.name());
                })
                .verify();
    }
}
