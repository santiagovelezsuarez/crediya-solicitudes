package co.pragma.r2dbc.adapter;

import co.pragma.exception.ErrorCode;
import co.pragma.exception.InfrastructureException;
import co.pragma.model.cliente.Cliente;
import co.pragma.model.cliente.gateways.ClienteRepository;
import co.pragma.r2dbc.entity.ClienteEntity;
import co.pragma.r2dbc.repository.ClienteReactiveRepository;
import co.pragma.r2dbc.helper.ReactiveAdapterOperations;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.UUID;

@Slf4j
@Repository
public class ClienteReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        Cliente,
        ClienteEntity,
        UUID,
        ClienteReactiveRepository
> implements ClienteRepository {

    public ClienteReactiveRepositoryAdapter(ClienteReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Cliente.class));
    }

    @Override
    public Mono<Cliente> findById(UUID id) {
        log.debug("Buscando cliente por id: {}", id);
        return repository.findById(id)
                .map(entity -> mapper.map(entity, Cliente.class))
                .doOnNext(cliente -> log.debug("Cliente encontrado: {}", cliente))
                .onErrorMap(ex -> new InfrastructureException(ErrorCode.DB_ERROR.name(), ex));
    }

    @Override
    public Flux<Cliente> findByIdIn(List<UUID> userIds) {
        log.debug("Buscando usuarios por id in: {}", userIds);
        return repository.findByIdIn(userIds)
                .map(entity -> mapper.map(entity, Cliente.class))
                .onErrorMap(ex -> new InfrastructureException(ErrorCode.DB_ERROR.name(), ex));
    }
}
