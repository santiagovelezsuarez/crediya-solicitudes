package co.pragma.r2dbc.repository;

import co.pragma.r2dbc.entity.ClienteEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import java.util.List;
import java.util.UUID;

public interface ClienteReactiveRepository extends ReactiveCrudRepository<ClienteEntity, UUID>, ReactiveQueryByExampleExecutor<ClienteEntity> {

    Flux<ClienteEntity> findByIdIn(List<UUID> clienteIds);
}
