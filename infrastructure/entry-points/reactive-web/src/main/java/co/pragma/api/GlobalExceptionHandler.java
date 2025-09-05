package co.pragma.api;

import co.pragma.api.dto.DtoValidationException;
import co.pragma.api.dto.ErrorResponse;
import co.pragma.exception.BusinessException;
import co.pragma.exception.DomainError;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final ErrorCodeHttpMapper errorCodeHttpMapper;

    @ExceptionHandler(BusinessException.class)
    public Mono<ResponseEntity<ErrorResponse>> handle(BusinessException ex, ServerHttpRequest request) {
        DomainError domainError = DomainError.from(ex);
        HttpStatus status = errorCodeHttpMapper.toHttpStatus(ex.getCode());

        log.warn("BusinessException: {} - {}", domainError.code(), ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .error(domainError.code())
                .message(domainError.message())
                .path(request.getPath().value())
                .build();

        return Mono.just(ResponseEntity
                .status(status)
                .body(error));
    }

    @ExceptionHandler(DtoValidationException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleValidationException(DtoValidationException ex, ServerHttpRequest request) {
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

        return Mono.just(
                ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(response)
        );
    }

    @ExceptionHandler(org.springframework.web.server.ServerWebInputException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleWebInputException(ServerWebInputException ex, ServerHttpRequest request) {
        log.warn("ServerWebInputException: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("INVALID_REQUEST_BODY")
                .message("El cuerpo de la solicitud está vacío o es inválido.")
                .path(request.getPath().value())
                .build();

        return Mono.just(ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGeneralException(Exception ex) {
        log.error("Unexpected error: ", ex);
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(java.time.Instant.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("INTERNAL_SERVER_ERROR")
                .message("An unexpected error occurred.")
                .path("")
                .build();
        return Mono.just(ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error));
    }
}

