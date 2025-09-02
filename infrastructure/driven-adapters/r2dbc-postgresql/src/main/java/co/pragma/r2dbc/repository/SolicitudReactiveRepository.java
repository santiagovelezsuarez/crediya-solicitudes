package co.pragma.r2dbc.repository;

import co.pragma.r2dbc.entity.SolicitudPrestamoEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface SolicitudReactiveRepository extends ReactiveCrudRepository<SolicitudPrestamoEntity, String>, ReactiveQueryByExampleExecutor<SolicitudPrestamoEntity> {

}
