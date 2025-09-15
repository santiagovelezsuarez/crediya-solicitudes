package co.pragma.r2dbc.adapter;

import co.pragma.exception.ErrorCode;
import co.pragma.exception.InfrastructureException;
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
        Short,
        EstadoSolicitudReactiveRepository
> implements EstadoSolicitudRepository {

    public EstadoSolicitudReactiveRepositoryAdapter(EstadoSolicitudReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, EstadoSolicitud.class));
    }

    @Override
    public Mono<EstadoSolicitud> findById(short id) {
        log.debug("Buscando estado de solicitud por ID: {}", id);
        return repository.findById(id)
                .map(entity -> mapper.map(entity, EstadoSolicitud.class))
                .doOnNext(entity -> log.trace("findById - Estado de solicitud encontrado: {}", entity))
                .onErrorMap(ex -> new InfrastructureException(ErrorCode.DB_ERROR.name(), ex));
    }

    @Override
    public Mono<EstadoSolicitud> findByNombre(String nombre) {
        log.debug("Buscando estado de solicitud por nombre: {}", nombre);
        return repository.findByNombre(nombre)
                .map(entity -> mapper.map(entity, EstadoSolicitud.class))
                .doOnNext(entity -> log.trace("findByNombre - Estado de solicitud encontrado: {}", entity))
                .onErrorMap(ex -> new InfrastructureException(ErrorCode.DB_ERROR.name(), ex));
    }
}
