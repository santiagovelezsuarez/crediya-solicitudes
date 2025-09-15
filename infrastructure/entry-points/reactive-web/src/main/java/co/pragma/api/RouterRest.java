package co.pragma.api;

import co.pragma.api.dto.response.ErrorResponse;
import co.pragma.api.dto.request.SolicitarPrestamoDTO;
import co.pragma.api.dto.response.SolicitudPrestamoResponseDTO;
import co.pragma.api.handler.SolicitudPrestamoHandler;
import co.pragma.model.solicitudprestamo.projection.SolicitudPrestamoRevision;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/solicitud-prestamo",
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.POST,
                    beanClass = SolicitudPrestamoHandler.class,
                    beanMethod = "listenCreateSolicitud",
                    operation = @Operation(
                            operationId = "crearSolicitudPrestamo",
                            summary = "Crear una nueva solicitud de préstamo para un cliente autenticado",
                            tags = {"Solicitud de Préstamo"},
                            security = { @SecurityRequirement(name = "bearerAuth") },
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = SolicitarPrestamoDTO.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Solicitud creada exitosamente",
                                            content = @Content(schema = @Schema(implementation = SolicitudPrestamoResponseDTO.class))),
                                    @ApiResponse(responseCode = "400", description = "Request inválido",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                                    @ApiResponse(responseCode = "404", description = "Tipo de préstamo no encontrado",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                                    @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/solicitud-prestamo",
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.GET,
                    beanClass = SolicitudPrestamoHandler.class,
                    beanMethod = "listenListSolicitudesPendientes",
                    operation = @Operation(
                            operationId = "listarSolicitudesPendientesRevision",
                            summary = "Listar solicitudes de préstamo pendientes de revisión manual",
                            description = "Retorna una lista paginada de solicitudes en estado Pendiente de revisión, Rechazadas o en Revisión manual. Solo accesible para usuarios con rol Asesor.",
                            tags = {"Solicitud de Préstamo"},
                            security = { @SecurityRequirement(name = "bearerAuth") },
                            parameters = {
                                    @Parameter(name = "page", description = "Número de página (por defecto 0)", in = ParameterIn.QUERY, schema = @Schema(type = "integer")),
                                    @Parameter(name = "size", description = "Tamaño de página (por defecto 10)", in = ParameterIn.QUERY, schema = @Schema(type = "integer"))
                            },
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Listado de solicitudes pendientes",
                                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = SolicitudPrestamoRevision.class)))),
                                    @ApiResponse(responseCode = "403", description = "No autorizado para acceder a este recurso",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                                    @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> solicitudPrestamoRoutes(SolicitudPrestamoHandler solicitudPrestamoHandler) {
        return route(POST("/api/v1/solicitud-prestamo"), solicitudPrestamoHandler::listenRegistrarSolicitud)
                .andRoute(GET("/api/v1/solicitud-prestamo"), solicitudPrestamoHandler::listenListarSolicitudesPendientes)
                .andRoute(PUT("/api/v1/solicitud-prestamo"), solicitudPrestamoHandler::listenAprobarSolicitud);
    }

    @Bean
    @RouterOperations(
            @RouterOperation(
                    path = "/api/health",
                    produces = {MediaType.TEXT_PLAIN_VALUE},
                    method = RequestMethod.GET,
                    operation = @Operation(
                            operationId = "healthCheck",
                            summary = "Health check",
                            description = "Verifica que el servicio está disponible",
                            tags = {"Health"},
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Servicio disponible")
                            }
                    )
            )
    )
    public RouterFunction<ServerResponse> healthRoutes() {
        return route(GET("/api/health"),
                request -> ServerResponse.ok().bodyValue("OK"));
    }
}
