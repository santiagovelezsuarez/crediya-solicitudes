package co.pragma.api.security;

import co.pragma.security.UserContextRequest;
import java.util.Optional;

public final class UserContextSupport {

    private static final String USER_CONTEXT_ATTR = "userContext";

    private UserContextSupport() {
        // Evita instanciación
    }

    public static UserContextRequest getContextOrThrow(org.springframework.web.reactive.function.server.ServerRequest request) {
        return request.attribute(USER_CONTEXT_ATTR)
                .map(UserContextRequest.class::cast)
                .orElseThrow(() -> new SecurityException("Missing user context"));
    }

    public static Optional<UserContextRequest> getContext(org.springframework.web.reactive.function.server.ServerRequest request) {
        return request.attribute(USER_CONTEXT_ATTR)
                .map(UserContextRequest.class::cast);
    }
}
