package co.pragma.api;

import co.pragma.api.dto.DtoValidatorBuilder;
import co.pragma.api.dto.SolicitarPrestamoDTO;
import co.pragma.api.dto.SolicitudPrestamoDtoMapper;
import co.pragma.api.dto.SolicitudPrestamoResponse;
import co.pragma.model.cliente.DocumentoIdentidadVO;
import co.pragma.model.estadosolicitud.gateways.EstadoSolicitudRepository;
import co.pragma.model.solicitudprestamo.SolicitudPrestamo;
import co.pragma.model.tipoprestamo.TipoPrestamo;
import co.pragma.model.tipoprestamo.TipoPrestamoVO;
import co.pragma.model.tipoprestamo.gateways.TipoPrestamoRepository;
import co.pragma.usecase.solicitud.SolicitudUseCase;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.endpoint.InvalidEndpointRequestException;
import org.springframework.http.HttpStatus;
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
    private final SolicitudPrestamoDtoMapper dtoMapper;
    private final Validator validator;
    private final TipoPrestamoRepository tipoPrestamoRepository;
    private final EstadoSolicitudRepository estadoPrestamoRepository;

    public Mono<ServerResponse> listenCreateSolicitud(ServerRequest serverRequest) {
        return serverRequest
                .bodyToMono(SolicitarPrestamoDTO.class)
                .doOnNext(dto -> log.info("Request Body: {}", dto))
                .flatMap(dto -> DtoValidatorBuilder.validate(dto, validator))
                .flatMap(dto -> {
                    var solicitud = dtoMapper.toModel(dto);
                    var documento = new DocumentoIdentidadVO(dto.getTipoDocumento(), dto.getNumeroDocumento());
                    var tipoPrestamoNombre = new TipoPrestamoVO(dto.getTipoPrestamo());

                    return solicitudUseCase.createSolicitud(solicitud, documento, tipoPrestamoNombre);
                })
                .flatMap(this::mapToResponse)
                .doOnNext(result -> log.info("Solicitud creada exitosamente con ID: {}", result.id()))
                .flatMap(response -> ServerResponse
                        .status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response));
    }

    private Mono<SolicitudPrestamoResponse> mapToResponse(SolicitudPrestamo solicitud) {
        return Mono.zip(
                tipoPrestamoRepository.findById(String.valueOf(solicitud.getIdTipoPrestamo())),
                estadoPrestamoRepository.findById(solicitud.getIdEstado())
        ).map(tuple -> {
            var tipoPrestamo = tuple.getT1();
            var estado = tuple.getT2();

            return new SolicitudPrestamoResponse(
                    solicitud.getId().toString(),
                    solicitud.getMonto(),
                    solicitud.getPlazoEnMeses(),
                    tipoPrestamo.getNombre(),
                    estado.getNombre()
            );
        });
    }
}
