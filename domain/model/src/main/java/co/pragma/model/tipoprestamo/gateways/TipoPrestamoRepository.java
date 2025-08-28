package co.pragma.model.tipoprestamo.gateways;

import co.pragma.model.tipoprestamo.TipoPrestamo;
import reactor.core.publisher.Mono;
import java.util.UUID;

public interface TipoPrestamoRepository {
    Mono<TipoPrestamo> findById(UUID id);
}
