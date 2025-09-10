package co.pragma.api.security;

import co.pragma.model.cliente.Session;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import reactor.core.publisher.Mono;
import java.util.Set;
import java.util.stream.Collectors;

public class SessionMapper {

    private SessionMapper() {}

    public static Mono<Session> fromSecurityContext() {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> toSession(ctx.getAuthentication()));
    }

    public static Session toSession(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return Session.builder()
                    .userId("")
                    .role("PUBLIC")
                    .permissions(Set.of())
                    .build();
        }

        String userId = (String) auth.getPrincipal();

        String role = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> a.startsWith("ROLE_"))
                .map(a -> a.replace("ROLE_", ""))
                .findFirst()
                .orElse("PUBLIC");

        Set<String> permissions = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> !a.startsWith("ROLE_"))
                .collect(Collectors.toSet());

        return Session.builder()
                .userId(userId)
                .role(role)
                .permissions(permissions)
                .build();
    }
}