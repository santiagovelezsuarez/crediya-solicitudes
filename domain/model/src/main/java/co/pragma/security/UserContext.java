package co.pragma.security;

import java.util.List;

public record UserContext(
        String userId,
        String email,
        String role,
        List<String> permissions
) {
    public boolean hasPermission(String permission) {
        return permissions != null && permissions.contains(permission);
    }

    public boolean isRole(String expectedRole) {
        return expectedRole != null && expectedRole.equalsIgnoreCase(role);
    }
}

