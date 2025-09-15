package co.pragma.model.session;

import co.pragma.exception.business.ForbiddenException;
import co.pragma.model.session.gateways.SessionProvider;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class PermissionValidator {

    private final SessionProvider sessionProvider;

    public Mono<Void> requirePermission(Permission permission) {
        return sessionProvider.getCurrentSession()
                .filter(session -> session.hasPermission(permission))
                .switchIfEmpty(Mono.error(new ForbiddenException()))
                .then();
    }
}
