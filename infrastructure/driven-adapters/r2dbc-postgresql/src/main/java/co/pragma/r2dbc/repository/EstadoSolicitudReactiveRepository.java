package co.pragma.r2dbc.repository;

import co.pragma.r2dbc.entity.EstadoSolicitudEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface EstadoSolicitudReactiveRepository extends ReactiveCrudRepository<EstadoSolicitudEntity, Short>, ReactiveQueryByExampleExecutor<EstadoSolicitudEntity> {

    Mono<EstadoSolicitudEntity> findByNombre(String nombre);
}
