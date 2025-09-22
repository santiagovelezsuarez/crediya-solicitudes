package co.pragma.api.handler;

import co.pragma.api.adapters.ResponseService;
import co.pragma.api.dto.DtoValidator;
import co.pragma.api.dto.request.AprobarSolicitudDTO;
import co.pragma.api.dto.request.SolicitarPrestamoDTO;
import co.pragma.api.mapper.SolicitudPrestamoDtoMapper;
import co.pragma.model.solicitudprestamo.command.SolicitarPrestamoCommand;
import co.pragma.usecase.solicitud.AprobarSolicitudPrestamoUseCase;
import co.pragma.usecase.solicitud.ListarSolicitudesRevisionManualUseCase;
import co.pragma.usecase.solicitud.SolicitarPrestamoUseCase;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class SolicitudPrestamoHandler {

    private final SolicitarPrestamoUseCase solicitarPrestamoUseCase;
    private final ListarSolicitudesRevisionManualUseCase listarSolicitudesRevisionManualUseCase;
    private final AprobarSolicitudPrestamoUseCase aprobarSolicitudPrestamoUseCase;
    private final SolicitudPrestamoDtoMapper mapper;
    private final DtoValidator dtoValidator;

    private final ResponseService responseService;

    public Mono<ServerResponse> listenRegistrarSolicitud(ServerRequest serverRequest) {
        log.debug("Petición recibida para registrar solicitud de prestamo");
        return serverRequest
                .bodyToMono(SolicitarPrestamoDTO.class)
                .flatMap(dtoValidator::validate)
                .flatMap(dto -> Mono.deferContextual(ctx -> {
                    String userId = ctx.get("userId");
                    SolicitarPrestamoCommand command = mapper.toCommand(dto, userId);
                    return solicitarPrestamoUseCase.execute(command);
                }))
                .doOnNext(s -> log.trace("Solicitud de préstamo registrada con éxito: {}", s.getId()))
                .map(mapper::toResponse)
                .flatMap(responseService::createdJson);
    }

    public Mono<ServerResponse> listenListarSolicitudesPendientes(ServerRequest serverRequest) {
        log.debug("Petición recibida para listar solicitudes pendientes");

        int page = serverRequest.queryParam("page").map(Integer::parseInt).orElse(0);
        int size = serverRequest.queryParam("size").map(Integer::parseInt).orElse(10);

        return listarSolicitudesRevisionManualUseCase.execute(page, size)
                .doOnNext(s -> log.trace("Solicitud listada: {}", s))
                .flatMap(responseService::okJson);
    }

    public Mono<ServerResponse> listenAprobarSolicitud(ServerRequest serverRequest) {
        log.debug("Petición recibida para aprobar/rechazar solicitud de credito");
        return serverRequest
                .bodyToMono(AprobarSolicitudDTO.class)
                .flatMap(dtoValidator::validate)
                .map(SolicitudPrestamoDtoMapper::toAprobarCommand)
                .flatMap(aprobarSolicitudPrestamoUseCase::execute)
                .doOnNext(solicitud -> log.trace("Solicitud {} actualizada", solicitud.getCodigo()))
                .map(mapper::toResponse)
                .flatMap(responseService::okJson);
    }
}
