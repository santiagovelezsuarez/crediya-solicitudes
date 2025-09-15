package co.pragma.api.security;

import co.pragma.model.session.Session;
import co.pragma.model.session.gateways.SessionProvider;
import reactor.core.publisher.Mono;

public class SecurityContextSessionProvider implements SessionProvider {

    @Override
    public Mono<Session> getCurrentSession() {
        return SessionMapper.fromSecurityContext();
    }
}
