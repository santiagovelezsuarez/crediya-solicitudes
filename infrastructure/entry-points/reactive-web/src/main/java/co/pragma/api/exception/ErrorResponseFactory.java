package co.pragma.api.exception;

import co.pragma.api.dto.response.ErrorResponse;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public class ErrorResponseFactory {

    private ErrorResponseFactory() {
    }

    public static ErrorResponse from(ErrorContext ctx) {
        return ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(ctx.status().value())
                .path(ctx.request().path())
                .error(ctx.errorCode().name())
                .message(ctx.message())
                .fieldErrors(Optional.ofNullable(ctx.fieldErrors()).orElse(List.of()))
                .build();
    }
}
