package co.pragma.model.solicitudprestamo.gateways;

import co.pragma.model.solicitudprestamo.SolicitudPrestamo;
import reactor.core.publisher.Mono;

public interface SolicitudPrestamoEventPublisher {
    Mono<Void> publishEstadoActualizado(SolicitudPrestamo solicitud);
}
