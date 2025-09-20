package co.pragma.api.security;

import co.pragma.security.PermissionEnum;
import co.pragma.security.UserContextRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class UserContextExtractor {

    private static final String HEADER_USER_ID = "x-user-id";
    private static final String HEADER_USER_ROLE = "x-user-role";
    private static final String HEADER_USER_EMAIL = "x-user-email";
    private static final String HEADER_USER_PERMISSIONS = "x-user-permissions";

    private UserContextExtractor(){}

    public UserContextRequest fromRequest(ServerRequest request) {
        String userId = request.headers().firstHeader(HEADER_USER_ID);
        String email = request.headers().firstHeader(HEADER_USER_EMAIL);
        String role = request.headers().firstHeader(HEADER_USER_ROLE);
        String permissionsHeader = request.headers().firstHeader(HEADER_USER_PERMISSIONS);

        Set<PermissionEnum> permissionEnums = permissionsHeader == null
                ? Collections.emptySet()
                : parsePermissions(permissionsHeader);

        return new UserContextRequest(userId, email, role, permissionEnums);
    }

    private Set<PermissionEnum> parsePermissions(String headerValue) {
        return Arrays.stream(headerValue.split(","))
                .map(String::trim)
                .map(this::safeParsePermission)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private PermissionEnum safeParsePermission(String permissionName) {
        try {
            return PermissionEnum.valueOf(permissionName.toUpperCase());
        } catch (IllegalArgumentException ex) {
            log.warn("Permiso desconocido o malformado recibido en cabecera: '{}'", permissionName);
            return null;
        }
    }
}
