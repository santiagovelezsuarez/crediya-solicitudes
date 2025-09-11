package co.pragma.model.solicitudprestamo.gateways;

import co.pragma.model.solicitudprestamo.SolicitudPrestamo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.List;

public interface SolicitudPrestamoRepository {
    Mono<SolicitudPrestamo> save(SolicitudPrestamo solicitudPrestamo);

    Flux<SolicitudPrestamo> findByIdEstadoIn(List<Short> estados, int page, int size);
}
