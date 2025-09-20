package co.pragma.security;

import java.util.Set;

public record UserContextRequest(
        String userId,
        String email,
        String role,
        Set<PermissionEnum> permissionEnums
) {}
