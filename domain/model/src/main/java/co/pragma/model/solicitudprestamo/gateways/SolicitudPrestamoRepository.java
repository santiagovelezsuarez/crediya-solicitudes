package co.pragma.model.solicitudprestamo.gateways;

import co.pragma.model.solicitudprestamo.SolicitudPrestamo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.UUID;

public interface SolicitudPrestamoRepository {
    Mono<SolicitudPrestamo> save(SolicitudPrestamo solicitudPrestamo);
    Mono<SolicitudPrestamo> findById(UUID id);
    Flux<SolicitudPrestamo> findByIdEstadoIn(List<Integer> estados, int page, int size);
}
