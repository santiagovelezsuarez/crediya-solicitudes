package co.pragma.r2dbc.adapter;

import co.pragma.model.estadosolicitud.EstadoSolicitud;
import co.pragma.model.estadosolicitud.gateways.EstadoSolicitudRepository;
import co.pragma.r2dbc.entity.EstadoSolicitudEntity;
import co.pragma.r2dbc.helper.ReactiveAdapterOperations;
import co.pragma.r2dbc.repository.EstadoSolicitudReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
public class EstadoSolicitudReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        EstadoSolicitud,
        EstadoSolicitudEntity,
        Integer,
        EstadoSolicitudReactiveRepository
> implements EstadoSolicitudRepository {

    public EstadoSolicitudReactiveRepositoryAdapter(EstadoSolicitudReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, EstadoSolicitud.class));
    }

    @Override
    public Mono<EstadoSolicitud> findById(int id) {
        log.info("Buscando estado de solicitud por ID: {}", id);
        return repository.findById(id)
                .map(entity -> mapper.map(entity, EstadoSolicitud.class))
                .doOnNext(entity -> log.info("Estado de solicitud encontrado: {}", entity))
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("No se encontró estado de solicitud con ID={}", id);
                    return Mono.empty();
                }))
                .doOnError(e -> log.error("Error al buscar estado de solicitud con ID {} : {}", id, e.getMessage()));
    }


    @Override
    public Mono<EstadoSolicitud> findByNombre(String nombre) {
        log.info("Buscando estado de solicitud por nombre: {}", nombre);
        return repository.findByNombre(nombre)
                .map(entity -> mapper.map(entity, EstadoSolicitud.class))
                .doOnNext(entity -> log.info("Estado de solicitud encontrado: {}", entity))
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("No se encontró estado de solicitud con nombre={}", nombre);
                    return Mono.empty();
                }))
                .doOnError(e -> log.error("Error al buscar estado de solicitud con nombre {} : {}", nombre, e.getMessage()));
    }
}
