package co.pragma.r2dbc.repository;

import co.pragma.r2dbc.entity.SolicitudPrestamoEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface SolicitudPrestamoReactiveRepository extends ReactiveCrudRepository<SolicitudPrestamoEntity, UUID>, ReactiveQueryByExampleExecutor<SolicitudPrestamoEntity> {

    @Query(value = "SELECT * FROM solicitudes WHERE id_estado IN (:estados) LIMIT :size OFFSET :offset")
    Flux<SolicitudPrestamoEntity> findByIdEstadoIn(@Param("estados") List<Integer> estados, @Param("size") int size, @Param("offset") int offset);

    @Query("UPDATE solicitudes SET notificado = :notificado WHERE codigo = :codigo")
    Mono<Void> markAsNotificado(@Param("codigo") String codigo, @Param("notificado") boolean notificado);

    Mono<SolicitudPrestamoEntity> findByCodigo(String codigo);

    //@Query("SELECT * FROM solicitudes WHERE id_cliente = :idCliente AND id_estado = :idEstado")
    Flux<SolicitudPrestamoEntity> findByIdClienteAndIdEstado(@Param("idCliente") UUID idCliente, @Param("idEstado") int idEstado);

}
