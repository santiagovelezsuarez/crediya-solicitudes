package co.pragma.model.session;

import lombok.Builder;
import lombok.Data;
import java.util.Set;

@Data
@Builder
public class Session {
    private final String userId;
    private final String role;
    private final String email;
    private final Set<String> permissions;

    public boolean hasPermission(Permission permission) {
        return permissions != null && permissions.contains(permission.name());
    }

    public boolean isAuthenticated() {
        return userId != null && !userId.isEmpty();
    }

    public boolean hasRole(String role) {
        return this.role != null && this.role.equalsIgnoreCase(role);
    }
}

