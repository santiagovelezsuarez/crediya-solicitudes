package co.pragma.model.tipoprestamo.gateways;

import co.pragma.model.tipoprestamo.TipoPrestamo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.UUID;

public interface TipoPrestamoRepository {
    Mono<TipoPrestamo> findById(String id);
    Mono<TipoPrestamo> findByNombre(String nombre);
    Flux<TipoPrestamo> findByIdIn(List<UUID> ids);
}
