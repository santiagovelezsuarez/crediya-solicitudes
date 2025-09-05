package co.pragma.model.solicitudprestamo.gateways;

import co.pragma.model.solicitudprestamo.SolicitudPrestamo;
import reactor.core.publisher.Mono;

public interface SolicitudPrestamoRepository {
    Mono<SolicitudPrestamo> save(SolicitudPrestamo solicitudPrestamo);
}
