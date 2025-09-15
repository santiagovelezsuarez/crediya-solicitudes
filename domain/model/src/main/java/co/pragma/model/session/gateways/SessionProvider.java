package co.pragma.model.session.gateways;

import co.pragma.model.session.Session;
import reactor.core.publisher.Mono;

public interface SessionProvider {
    Mono<Session> getCurrentSession();
}
