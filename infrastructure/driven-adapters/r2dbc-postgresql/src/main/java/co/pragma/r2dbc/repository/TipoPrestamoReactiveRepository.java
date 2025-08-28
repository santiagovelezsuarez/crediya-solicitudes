package co.pragma.r2dbc.repository;

import co.pragma.r2dbc.entity.TipoPrestamoEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface TipoPrestamoReactiveRepository extends ReactiveCrudRepository<TipoPrestamoEntity, UUID>, ReactiveQueryByExampleExecutor<TipoPrestamoEntity> {

}
