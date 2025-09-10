package co.pragma.model.cliente;

import co.pragma.exception.business.ForbiddenException;
import co.pragma.model.cliente.gateways.SessionProvider;
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
