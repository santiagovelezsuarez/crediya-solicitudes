package co.pragma.model.estadosolicitud.gateways;

import co.pragma.model.estadosolicitud.EstadoSolicitud;
import reactor.core.publisher.Mono;

public interface EstadoSolicitudRepository {
    Mono<EstadoSolicitud> findByNombre(String nombre);
}