package co.pragma.api;

import co.pragma.base.exception.BusinessException;
import co.pragma.base.exception.TipoPrestamoNotExistsException;
import common.api.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;
import java.time.Instant;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TipoPrestamoNotExistsException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleTipoPrestamoNotExistsException(TipoPrestamoNotExistsException ex, ServerHttpRequest request) {
        log.info("TipoPrestamoNotExistsException: {}", ex.getMessage());
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.CONFLICT.value())
                .error("TIPO_PRESTAMO_NOT_EXISTS")
                .message(ex.getMessage())
                .path(request.getPath().value())
                .build();
        return Mono.just(ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error));
    }

    @ExceptionHandler(BusinessException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleBusinessException(BusinessException ex, ServerHttpRequest request) {
        log.warn("BusinessException: {}", ex.getMessage());
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("BUSINESS_ERROR")
                .message(ex.getMessage())
                .path(request.getPath().value())
                .build();
        return Mono.just(ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error));
    }

    @ExceptionHandler(common.api.exception.DtoValidationException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleValidationException(common.api.exception.DtoValidationException ex, ServerHttpRequest request) {
        log.info("DtoValidationException: {}", ex.getMessage());

        List<ErrorResponse.FieldError> fieldErrors = ex.getErrors().stream()
                .map(err -> new ErrorResponse.FieldError(err.field(), err.message()))
                .toList();

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("VALIDATION_ERROR")
                .message("Existen errores de validación")
                .path(request.getPath().value())
                .validationErrors(fieldErrors)
                .build();

        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response));

    }

    @ExceptionHandler(ServerWebInputException.class)
    public ResponseEntity<ErrorResponse> handleInvalidUUID(ServerWebInputException ex, ServerHttpRequest request) {
        log.info("ServerWebInputException: {}", ex.getMessage());
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("BAD_REQUEST")
                .message("Invalid input: " + ex.getReason())
                .path(request.getPath().value())
                .build();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }


    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGeneralException(Exception ex, ServerHttpRequest request) {
        log.error("Unexpected error: ", ex);
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("INTERNAL_SERVER_ERROR")
                .message("An unexpected error occurred.")
                .path(request.getPath().value())
                .build();
        return Mono.just(ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error));
    }
}

