package co.pragma.r2dbc.adapter;

import co.pragma.model.solicitudprestamo.SolicitudPrestamo;
import co.pragma.model.solicitudprestamo.gateways.SolicitudPrestamoRepository;
import co.pragma.r2dbc.entity.SolicitudPrestamoEntity;
import co.pragma.r2dbc.repository.SolicitudReactiveRepository;
import co.pragma.r2dbc.helper.ReactiveAdapterOperations;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
public class SolicitudReactivePrestamoRepositoryAdapter extends ReactiveAdapterOperations<
        SolicitudPrestamo,
        SolicitudPrestamoEntity,
        String,
        SolicitudReactiveRepository
> implements SolicitudPrestamoRepository {
    public SolicitudReactivePrestamoRepositoryAdapter(SolicitudReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, SolicitudPrestamo.class));
    }

    @Override
    public Mono<SolicitudPrestamo> save(SolicitudPrestamo solicitud) {
        log.info("Registrando solicitud: {}", solicitud.toString());
        return super.save(solicitud)
                .doOnSuccess(s -> log.info("Solicitud registrada con éxito: {}", s))
                .doOnError(e -> log.error("Error al registrar solicitud: {}", e.getMessage()));
    }

}
