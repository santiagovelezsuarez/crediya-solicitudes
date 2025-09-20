package co.pragma.usecase.security;

import co.pragma.security.PermissionEnum;
import co.pragma.security.UserContextRequest;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class PermissionValidator {

    public Mono<Void> requirePermission(UserContextRequest context, PermissionEnum requiredPermissionEnum) {
        if (context.permissionEnums().contains(requiredPermissionEnum)) {
            return Mono.empty();
        }
        return Mono.error(new SecurityException("Permiso denegado: " + requiredPermissionEnum));
    }
}
