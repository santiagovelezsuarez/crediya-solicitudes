package co.pragma.r2dbc.adapter;

import co.pragma.exception.ErrorCode;
import co.pragma.exception.InfrastructureException;
import co.pragma.model.solicitudprestamo.SolicitudPrestamo;
import co.pragma.model.solicitudprestamo.gateways.SolicitudPrestamoRepository;
import co.pragma.r2dbc.mapper.SolicitudPrestamoEntityMapper;
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

    @Override
    public Mono<SolicitudPrestamo> save(SolicitudPrestamo solicitud) {
        log.debug("Registrando solicitud: {}", solicitud);
        return repository.save(SolicitudPrestamoEntityMapper.toEntity(solicitud))
                .map(SolicitudPrestamoEntityMapper::toDomain)
                .doOnNext(s -> log.trace("Solicitud registrada con éxito: {}", s.getId()))
                .onErrorMap(ex -> new InfrastructureException(ErrorCode.DB_ERROR.name(), ex));
    }

    @Override
    public Mono<SolicitudPrestamo> findByCodigo(String codigo) {
        log.debug("Buscando Solicitud Prestamo by codigo: {}", codigo);
        return repository.findByCodigo(codigo)
                .map(SolicitudPrestamoEntityMapper::toDomain);
    }

    @Override
    public Flux<SolicitudPrestamo> findByIdEstadoIn(List<Integer> estados, int page, int size) {
        log.debug("Buscando solicitudes de prestamo ByEstadoSolicitud");
        int offset = page * size;
        return repository.findByIdEstadoIn(estados, size, offset)
                .map(SolicitudPrestamoEntityMapper::toDomain)
                .onErrorMap(ex -> new InfrastructureException(ErrorCode.DB_ERROR.name(), ex));
    }

    @Override
    public Mono<Void> markAsNotificado(String codigo, Boolean notificado) {
        log.info("Marcando solicitud {} como notificada en {}", codigo, notificado);
        return repository.markAsNotificado(codigo, notificado);
    }

}
