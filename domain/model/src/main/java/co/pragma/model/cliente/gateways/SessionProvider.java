package co.pragma.model.cliente.gateways;

import co.pragma.model.cliente.Session;
import reactor.core.publisher.Mono;

public interface SessionProvider {
    Mono<Session> getCurrentSession();
}
