package co.pragma.r2dbc.adapter;

import co.pragma.model.solicitud.Solicitud;
import co.pragma.model.solicitud.gateways.SolicitudRepository;
import co.pragma.r2dbc.entity.SolicitudEntity;
import co.pragma.r2dbc.repository.SolicitudReactiveRepository;
import co.pragma.r2dbc.helper.ReactiveAdapterOperations;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
public class SolicitudReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        Solicitud,
        SolicitudEntity,
        String,
        SolicitudReactiveRepository
> implements SolicitudRepository {
    public SolicitudReactiveRepositoryAdapter(SolicitudReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Solicitud.class));
    }

    @Override
    public Mono<Solicitud> save(Solicitud solicitud) {
        log.info("Registrando solicitud: {}", solicitud.toString());
        return super.save(solicitud)
                .doOnSuccess(s -> log.info("Solicitud registrada con éxito: {}", s))
                .doOnError(e -> log.error("Error al registrar solicitud: {}", e.getMessage()));
    }

}
