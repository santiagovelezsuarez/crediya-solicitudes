package co.pragma.r2dbc.adapter;

import co.pragma.error.ErrorCode;
import co.pragma.exception.InfrastructureException;
import co.pragma.model.solicitudprestamo.SolicitudPrestamo;
import co.pragma.model.solicitudprestamo.gateways.SolicitudPrestamoRepository;
import co.pragma.r2dbc.mapper.SolicitudPrestamoMapper;
import co.pragma.r2dbc.repository.SolicitudPrestamoReactiveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class SolicitudPrestamoReactiveRepositoryAdapter implements SolicitudPrestamoRepository {

    private final SolicitudPrestamoReactiveRepository repository;
    private final SolicitudPrestamoMapper mapper;

    @Override
    public Mono<SolicitudPrestamo> save(SolicitudPrestamo solicitud) {
        log.debug("Registrando solicitud: {}", solicitud);
        return repository.save(mapper.toEntity(solicitud))
                .map(mapper::toDomain)
                .doOnSuccess(s -> log.trace("Solicitud registrada con éxito: {}", s.getId()))
                .onErrorMap(ex -> new InfrastructureException(ErrorCode.DB_ERROR.name(), ex));
    }

    @Override
    public Flux<SolicitudPrestamo> findByIdEstadoIn(List<Short> estados, int page, int size) {
        log.debug("Buscando solicitudes de prestamo ByEstadoSolicitud");
        int offset = page * size;
        return repository.findByIdEstadoIn(estados, size, offset)
                .map(mapper::toDomain)
                .onErrorMap(ex -> new InfrastructureException(ErrorCode.DB_ERROR.name(), ex));
    }
}
