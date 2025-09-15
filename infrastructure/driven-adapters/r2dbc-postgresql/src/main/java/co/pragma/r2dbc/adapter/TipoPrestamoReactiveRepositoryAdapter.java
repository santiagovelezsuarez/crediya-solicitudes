package co.pragma.r2dbc.adapter;

import co.pragma.exception.ErrorCode;
import co.pragma.exception.InfrastructureException;
import co.pragma.model.tipoprestamo.TipoPrestamo;
import co.pragma.model.tipoprestamo.projection.TipoPrestamoInfo;
import co.pragma.model.tipoprestamo.gateways.TipoPrestamoRepository;
import co.pragma.r2dbc.entity.TipoPrestamoEntity;
import co.pragma.r2dbc.helper.ReactiveAdapterOperations;
import co.pragma.r2dbc.repository.TipoPrestamoReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.UUID;

@Slf4j
@Repository
public class TipoPrestamoReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        TipoPrestamo,
        TipoPrestamoEntity,
        UUID,
        TipoPrestamoReactiveRepository
> implements TipoPrestamoRepository {

    public TipoPrestamoReactiveRepositoryAdapter(TipoPrestamoReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, TipoPrestamo.class));
    }

    @Override
    public Mono<TipoPrestamo> findById(String id) {
        log.debug("TipoPrestamoReactiveRepositoryAdapter.findById {}", id);
        return repository.findById(UUID.fromString(id))
                .map(entity -> mapper.map(entity, TipoPrestamo.class))
                .onErrorMap(ex -> new InfrastructureException(ErrorCode.DB_ERROR.name(), ex));
    }

    @Override
    public Mono<TipoPrestamo> findByNombre(String nombre) {
        log.debug("Buscando tipo de préstamo por NOMBRE: {}", nombre);
        return repository.findByNombre(nombre)
                .map(entity -> mapper.map(entity, TipoPrestamo.class))
                .onErrorMap(ex -> new InfrastructureException(ErrorCode.DB_ERROR.name(), ex));
    }

    @Override
    public Flux<TipoPrestamoInfo> findByIdIn(List<UUID> ids) {
        log.debug("Buscando tipo de préstamo por Ids");
        return repository.findByIdIn(ids)
                .map(entity -> mapper.map(entity, TipoPrestamoInfo.class))
                .onErrorMap(ex -> new InfrastructureException(ErrorCode.DB_ERROR.name(), ex));
    }
}
