package co.pragma.api;

import co.pragma.api.dto.ErrorResponse;
import co.pragma.exception.business.ClienteNotFoundException;
import co.pragma.exception.business.TipoPrestamoNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class GlobalExceptionSolicitudPrestamoHandlerTest {

    private final ErrorCodeHttpMapper errorCodeHttpMapper = new ErrorCodeHttpMapper();
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler(errorCodeHttpMapper);

    @Test
    void shouldHandleClienteNotFound() {
        var ex = new ClienteNotFoundException("Cliente no encontrado");
        ServerHttpRequest request = MockServerHttpRequest.get("/usuarios").build();

        var result = handler.handle(ex, request);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    ErrorResponse body = response.getBody();
                    assertThat(body).isNotNull();
                    Assertions.assertNotNull(body);
                    assertThat(body.getError()).isEqualTo(ErrorCode.CLIENTE_NOT_FOUND.name());
                    assertThat(body.getMessage()).isEqualTo("Cliente no encontrado");
                })
                .verifyComplete();
    }

    @Test
    void shouldHandleTipoPrestamoNotFound() {
        var ex = new TipoPrestamoNotFoundException("Tipo de préstamo no encontrado");
        ServerHttpRequest request = MockServerHttpRequest.get("/prestamos").build();

        var result = handler.handle(ex, request);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    ErrorResponse body = response.getBody();
                    assertThat(body).isNotNull();
                    Assertions.assertNotNull(body);
                    assertThat(body.getError()).isEqualTo(ErrorCode.TIPO_PRESTAMO_NOT_FOUND.name());
                    assertThat(body.getMessage()).isEqualTo("Tipo de préstamo no encontrado");
                })
                .verifyComplete();
    }

}
