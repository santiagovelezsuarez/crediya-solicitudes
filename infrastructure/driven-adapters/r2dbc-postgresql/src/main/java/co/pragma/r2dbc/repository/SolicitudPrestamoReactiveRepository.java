package co.pragma.r2dbc.repository;

import co.pragma.r2dbc.entity.SolicitudPrestamoEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface SolicitudPrestamoReactiveRepository extends ReactiveCrudRepository<SolicitudPrestamoEntity, UUID>, ReactiveQueryByExampleExecutor<SolicitudPrestamoEntity> {

    @Query("SELECT * FROM solicitudes WHERE id_estado IN (:estados) LIMIT :size OFFSET :offset")
    Flux<SolicitudPrestamoEntity> findByIdEstadoIn(List<Integer> estados, int size, int offset);

    @Query("UPDATE solicitudes SET id_estado = 2 WHERE id_solicitud = :id")
    Mono<Integer> updateEstadoById(UUID id, Integer idEstado);

}
