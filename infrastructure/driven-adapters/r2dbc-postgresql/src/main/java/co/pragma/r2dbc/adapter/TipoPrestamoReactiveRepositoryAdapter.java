package co.pragma.r2dbc.adapter;

import co.pragma.model.tipoprestamo.TipoPrestamo;
import co.pragma.model.tipoprestamo.gateways.TipoPrestamoRepository;
import co.pragma.r2dbc.entity.TipoPrestamoEntity;
import co.pragma.r2dbc.helper.ReactiveAdapterOperations;
import co.pragma.r2dbc.repository.TipoPrestamoReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

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
    public Mono<TipoPrestamo> findById(UUID id) {
        log.info("Buscando tipo de préstamo por Id: {}", id);
        return repository.findById(id)
                .map(entity -> mapper.map(entity, TipoPrestamo.class))
                .doOnNext(entity -> log.info("Tipo de préstamo encontrado: {}", entity))
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("No se encontró tipo de préstamo con Id={}", id);
                    return Mono.empty();
                }))
                .doOnError(e -> log.error("Error al buscar tipo de préstamo con nombre {} : {}", id, e.getMessage()));
    }
}
