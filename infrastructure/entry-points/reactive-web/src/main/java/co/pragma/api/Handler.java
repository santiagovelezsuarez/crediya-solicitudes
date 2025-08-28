package co.pragma.api;

import co.pragma.api.dto.SolicitudPrestamoDtoMapper;
import co.pragma.api.dto.SolicitudPrestamoRequest;
import co.pragma.usecase.solicitud.SolicitudUseCase;
import common.api.dto.ValidationUtil;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class Handler {

    private final SolicitudUseCase solicitudUseCase;
    private final Validator validator;
    private final SolicitudPrestamoDtoMapper solicitudPrestamoDtoMapper;

    public Mono<ServerResponse> listenCreateSolicitud(ServerRequest serverRequest) {
        log.info("Petición recibida para crear solicitud");
        return serverRequest
                .bodyToMono(SolicitudPrestamoRequest.class)
                .doOnNext(req -> log.info("Request Body: {}", req))
                .flatMap(dto -> ValidationUtil.validate(dto, validator))
                .map(solicitudPrestamoDtoMapper::toModel)
                .flatMap(solicitudUseCase::createSolicitud)
                .map(solicitudPrestamoDtoMapper::toResponse)
                .flatMap(this::buildSuccessResponse)
                .doOnError(this::handleError);
    }

    private Mono<ServerResponse> buildSuccessResponse(Object responseBody) {
        log.info("Solicitud creada exitosamente");
        return ServerResponse
                .status(201)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(responseBody);
    }

    private void handleError(Throwable err) {
        log.error("Error al crear solicitud: {}", err.getMessage());
    }
}
