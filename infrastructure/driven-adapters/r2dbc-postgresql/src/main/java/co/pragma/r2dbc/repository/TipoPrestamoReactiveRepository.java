package co.pragma.r2dbc.repository;

import co.pragma.r2dbc.entity.TipoPrestamoEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.UUID;

public interface TipoPrestamoReactiveRepository extends ReactiveCrudRepository<TipoPrestamoEntity, UUID>, ReactiveQueryByExampleExecutor<TipoPrestamoEntity> {

    Mono<TipoPrestamoEntity> findByNombre(String nombre);
    Flux<TipoPrestamoEntity> findByIdIn(List<UUID> ids);
}
