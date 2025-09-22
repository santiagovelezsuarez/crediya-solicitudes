package co.pragma.api.security;

import co.pragma.security.PermissionEnum;
import co.pragma.security.UserContextRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.server.ServerRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserContextExtractorTest {

    private UserContextExtractor extractor;
    private ServerRequest request;
    private ServerRequest.Headers headers;

    @BeforeEach
    void setUp() {
        extractor = new UserContextExtractor();
        request = mock(ServerRequest.class);
        headers = mock(ServerRequest.Headers.class);
        when(request.headers()).thenReturn(headers);
    }

    @Test
    void fromRequestShouldExtractHeadersAndParsePermissions() {
        mockHeaders();

        UserContextRequest userContext = extractor.fromRequest(request);

        assertEquals("test-user-id", userContext.userId());
        assertEquals("user@test.com", userContext.email());
        assertEquals("TEST_ROLE", userContext.role());
        assertEquals(1, userContext.permissionEnums().size());
        assertTrue(userContext.permissionEnums().contains(PermissionEnum.LISTAR_SOLICITUDES_PENDIENTES));
    }

    private void mockHeaders() {
        when(headers.firstHeader("x-user-id")).thenReturn("test-user-id");
        when(headers.firstHeader("x-user-email")).thenReturn("user@test.com");
        when(headers.firstHeader("x-user-role")).thenReturn("TEST_ROLE");
        when(headers.firstHeader("x-user-permissions")).thenReturn("LISTAR_SOLICITUDES_PENDIENTES");
    }
}
