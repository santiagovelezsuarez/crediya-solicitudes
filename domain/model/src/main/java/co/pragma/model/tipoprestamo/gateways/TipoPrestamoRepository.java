package co.pragma.model.tipoprestamo.gateways;

import co.pragma.model.tipoprestamo.TipoPrestamo;
import reactor.core.publisher.Mono;

public interface TipoPrestamoRepository {
    Mono<TipoPrestamo> findById(String id);
    Mono<TipoPrestamo> findByNombre(String nombre);
}
