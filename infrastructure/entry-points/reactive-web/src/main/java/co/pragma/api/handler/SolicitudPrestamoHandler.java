package co.pragma.api.handler;

import co.pragma.api.adapters.ResponseService;
import co.pragma.api.dto.SolicitarPrestamoDTO;
import co.pragma.api.dto.SolicitudPrestamoDtoMapper;
import co.pragma.model.cliente.Permission;
import co.pragma.model.cliente.PermissionValidator;
import co.pragma.model.cliente.gateways.SessionProvider;
import co.pragma.usecase.solicitud.SolicitudPrestamoUseCase;
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

    private final SolicitudPrestamoUseCase solicitudPrestamoUseCase;
    private final ResponseService responseService;
    private final SolicitudPrestamoDtoMapper solicitudDtoMapper;
    private final PermissionValidator permissionValidator;
    private final SolicitudPrestamoDtoMapper solicitudPrestamoDtoMapper;
    private final SessionProvider sessionProvider;

    public Mono<ServerResponse> listenCreateSolicitud(ServerRequest serverRequest) {
        log.debug("Petición recibida para crear solicitud de prestamo");
        return serverRequest
                .bodyToMono(SolicitarPrestamoDTO.class)
                .flatMap(dto -> permissionValidator
                        .requirePermission(Permission.SOLICITAR_PRESTAMO)
                        .then(sessionProvider.getCurrentSession())
                        .map(session -> solicitudPrestamoDtoMapper.toCommand(dto, session.getUserId()))
                )
                .flatMap(solicitudPrestamoUseCase::execute)
                .doOnNext(s -> log.trace("Solicitud de préstamo registrada con éxito: {}", s.getId()))
                .map(solicitudDtoMapper::toResponse)
                .flatMap(responseService::createdJson);
    }
}
