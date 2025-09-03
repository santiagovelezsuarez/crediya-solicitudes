package co.pragma.r2dbc.adapter;

import co.pragma.model.solicitudprestamo.SolicitudPrestamo;
import co.pragma.model.solicitudprestamo.gateways.SolicitudPrestamoRepository;
import co.pragma.r2dbc.entity.SolicitudPrestamoEntity;
import co.pragma.r2dbc.mapper.SolicitudPrestamoMapper;
import co.pragma.r2dbc.repository.SolicitudReactiveRepository;
import co.pragma.r2dbc.helper.ReactiveAdapterOperations;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
@RequiredArgsConstructor
public class SolicitudReactivePrestamoRepositoryAdapter implements SolicitudPrestamoRepository {

    private final SolicitudReactiveRepository repository;
    private final SolicitudPrestamoMapper mapper;

    @Override
    public Mono<SolicitudPrestamo> save(SolicitudPrestamo solicitud) {
        log.info("Registrando solicitud: {}", solicitud);

        SolicitudPrestamoEntity entity = mapper.toEntity(solicitud);

        return repository.save(entity)
                .map(mapper::toModel)
                .doOnSuccess(s -> log.info("Solicitud registrada con éxito: {}", s))
                .doOnError(e -> log.error("Error al registrar solicitud: {}", e.getMessage()));
    }

}
