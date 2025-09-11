package co.pragma.r2dbc.repository;

import co.pragma.r2dbc.entity.SolicitudPrestamoEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.List;

public interface SolicitudPrestamoReactiveRepository extends ReactiveCrudRepository<SolicitudPrestamoEntity, String>, ReactiveQueryByExampleExecutor<SolicitudPrestamoEntity> {

    @Query("SELECT * FROM solicitudes WHERE id_estado IN (:estados) LIMIT :size OFFSET :offset")
    Flux<SolicitudPrestamoEntity> findByIdEstadoIn(List<Short> estados, int size, int offset);

}
