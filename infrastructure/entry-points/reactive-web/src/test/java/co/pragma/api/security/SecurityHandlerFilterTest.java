package co.pragma.api.security;

import co.pragma.security.PermissionEnum;
import co.pragma.security.UserContextRequest;
import co.pragma.usecase.security.PermissionValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Set;

import static org.mockito.Mockito.*;

class SecurityHandlerFilterTest {

    private PermissionValidator permissionValidator;
    private UserContextExtractor userContextExtractor;
    private SecurityHandlerFilter securityHandlerFilter;

    @BeforeEach
    void setUp() {
        permissionValidator = mock(PermissionValidator.class);
        userContextExtractor = mock(UserContextExtractor.class);
        securityHandlerFilter = new SecurityHandlerFilter(permissionValidator, userContextExtractor);
    }

    @Test
    void shouldThrowSecurityExceptionWhenUserContextIsNull() {
        PermissionEnum requiredPermission = PermissionEnum.APROBAR_SOLICITUD;
        ServerRequest request = mock(ServerRequest.class);
        HandlerFunction<ServerResponse> next = mock(HandlerFunction.class);

        when(userContextExtractor.fromRequest(request)).thenReturn(null);

        Mono<ServerResponse> responseMono = securityHandlerFilter.requirePermission(requiredPermission).filter(request, next);

        StepVerifier.create(responseMono)
                .expectErrorMatches(throwable -> throwable instanceof SecurityException &&
                        throwable.getMessage().equals("User context could not be extracted"))
                .verify();

        verify(next, never()).handle(request);
    }

    @Test
    void shouldProceedToNextHandlerWhenPermissionGranted() {
        PermissionEnum requiredPermission = PermissionEnum.APROBAR_SOLICITUD;
        UserContextRequest userContext = new UserContextRequest(
                "1", "test@test.com", "USER", Set.of(PermissionEnum.APROBAR_SOLICITUD)
        );
        ServerRequest request = mock(ServerRequest.class);
        ServerResponse mockResponse = mock(ServerResponse.class);
        HandlerFunction<ServerResponse> next = mock(HandlerFunction.class);

        when(userContextExtractor.fromRequest(request)).thenReturn(userContext);
        when(permissionValidator.requirePermission(userContext, requiredPermission)).thenReturn(Mono.empty());
        when(next.handle(request)).thenReturn(Mono.just(mockResponse));

        Mono<ServerResponse> responseMono = securityHandlerFilter.requirePermission(requiredPermission).filter(request, next);

        StepVerifier.create(responseMono)
                .expectNext(mockResponse)
                .verifyComplete();

        verify(next, times(1)).handle(request);
    }
}
