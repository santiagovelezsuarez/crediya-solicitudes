package co.pragma.api.exception;

import co.pragma.api.ErrorCodeHttpMapper;
import co.pragma.api.dto.DtoValidationException;
import co.pragma.api.dto.response.ErrorResponse;
import co.pragma.exception.ErrorCode;
import co.pragma.exception.InfrastructureException;
import co.pragma.exception.business.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.*;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@Order(-2)
public class GlobalExceptionHandler extends AbstractErrorWebExceptionHandler {

    private final ErrorCodeHttpMapper errorCodeHttpMapper;

    public GlobalExceptionHandler(ErrorAttributes errorAttributes,
                                  ApplicationContext applicationContext,
                                  ServerCodecConfigurer codecConfigurer,
                                  ErrorCodeHttpMapper errorCodeHttpMapper) {
        super(errorAttributes, new WebProperties.Resources(), applicationContext);
        this.setMessageWriters(codecConfigurer.getWriters());
        this.setMessageReaders(codecConfigurer.getReaders());
        this.errorCodeHttpMapper = errorCodeHttpMapper;
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::buildErrorResponse);
    }

    protected Mono<ServerResponse> buildErrorResponse(ServerRequest request) {
        return Mono.error(getError(request))
                .onErrorResume(BusinessException.class, ex -> handleBusinessException(ex, request))
                .onErrorResume(InfrastructureException.class, ex -> handleInfrastructureException(request))
                .onErrorResume(DtoValidationException.class, ex -> handleValidationException(ex, request))
                .onErrorResume(ServerWebInputException.class, ex -> handleWebInputException(request))
                .onErrorResume(ex -> handleDefaultException(request)).cast(ServerResponse.class);
    }

    private Mono<ServerResponse> handleBusinessException(BusinessException ex, ServerRequest request) {
        HttpStatus status = errorCodeHttpMapper.toHttpStatus(ex.getCode());
        return buildResponse(ErrorContext.of(request, status, ex.getCode(), ex.getMessage()));
    }

    private Mono<ServerResponse> handleInfrastructureException(ServerRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return buildResponse(ErrorContext.of(request, status, ErrorCode.TECHNICAL_ERROR));
    }

    private Mono<ServerResponse> handleValidationException(DtoValidationException ex, ServerRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        List<ErrorResponse.FieldError> fieldErrors = ex.getErrors().stream()
                .map(err -> new ErrorResponse.FieldError(err.getField(), err.getMessage()))
                .toList();

        return buildResponse(ErrorContext.ofValidation(request, status, ErrorCode.INVALID_INPUT, fieldErrors));

    }

    private Mono<ServerResponse> handleWebInputException(ServerRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return buildResponse(ErrorContext.of(request, status, ErrorCode.INVALID_REQUEST));
    }

    private Mono<ServerResponse>  handleDefaultException(ServerRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return buildResponse(ErrorContext.of(request, status, ErrorCode.INTERNAL_SERVER_ERROR));
    }

    private Mono<ServerResponse> buildResponse(ErrorContext context) {
        ErrorResponse body = ErrorResponseFactory.from(context);

        return ServerResponse.status(context.status())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body);
    }

    private String extractRootCause(Throwable ex) {
        return Optional.ofNullable(ex.getCause())
                .map(Throwable::getCause)
                .map(Throwable::getMessage)
                .orElse(ex.getMessage());
    }

    @Override
    protected void logError(ServerRequest request, ServerResponse response, Throwable ex) {
        String path = request.path();
        HttpMethod method = request.method();
        String rootCauseMsg = extractRootCause(ex);

        if (ex instanceof BusinessException be)
            log.warn("BusinessException at {} {}: {} - code={}", method, path, be.getMessage(), be.getCode());

        else if (ex instanceof InfrastructureException ie)
            log.error("InfrastructureException at {} {}: {} (cause: {})", method, path, ie.getMessage(), rootCauseMsg);

        else if (ex instanceof DtoValidationException ve)
            log.info("Validation error at {} {}: {}", method, path, ve.getMessage());

        else if (ex instanceof ServerWebInputException we)
            log.warn("Web input error at {} {}: {}", method, path, we.getMessage());

        else
            log.error("Unexpected error at {} {}: ", method, path, ex);
    }
}
