package co.pragma.api.exception;

import co.pragma.api.dto.response.ErrorResponse;
import co.pragma.exception.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.List;

public record ErrorContext(
        ServerRequest request,
        HttpStatus status,
        ErrorCode errorCode,
        String message,
        List<ErrorResponse.FieldError> fieldErrors
) {
    public static ErrorContext of(ServerRequest request,
                                  HttpStatus status,
                                  ErrorCode errorCode) {
        return new ErrorContext(request, status, errorCode, errorCode.getDefaultMessage(), List.of());
    }

    public static ErrorContext of(ServerRequest request,
                                  HttpStatus status,
                                  ErrorCode errorCode,
                                  String customMessage) {
        return new ErrorContext(request, status, errorCode, customMessage, List.of());
    }

    public static ErrorContext ofValidation(ServerRequest request,
                                            HttpStatus status,
                                            ErrorCode errorCode,
                                            List<ErrorResponse.FieldError> fieldErrors) {
        return new ErrorContext(request, status, errorCode, errorCode.getDefaultMessage(), fieldErrors);
    }
}
