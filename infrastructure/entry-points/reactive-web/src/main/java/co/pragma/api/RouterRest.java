package co.pragma.api;

import co.pragma.api.dto.response.ErrorResponse;
import co.pragma.api.dto.request.SolicitarPrestamoDTO;
import co.pragma.api.dto.response.SolicitudPrestamoResponseDTO;
import co.pragma.api.handler.SolicitudPrestamoHandler;
import co.pragma.api.security.SecurityHandlerFilter;
import co.pragma.model.solicitudprestamo.projection.SolicitudPrestamoRevision;
import co.pragma.security.PermissionEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class RouterRest {

    private final SecurityHandlerFilter securityFilter;
    private static final String ROUTE = "/api/v1/solicitud-prestamo";

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/solicitud-prestamo",
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.POST,
                    beanClass = SolicitudPrestamoHandler.class,
                    beanMethod = "listenRegistrarSolicitud", // corregido
                    operation = @Operation(
                            operationId = "crearSolicitudPrestamo",
                            summary = "Crear una nueva solicitud de préstamo",
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
                    beanMethod = "listenListarSolicitudesPendientes", // corregido
                    operation = @Operation(
                            operationId = "listarSolicitudesPendientesRevision",
                            summary = "Listar solicitudes pendientes",
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
            ),
            @RouterOperation(
                    path = "/api/v1/solicitud-prestamo",
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.PUT,
                    beanClass = SolicitudPrestamoHandler.class,
                    beanMethod = "listenAprobarSolicitud", // agregado
                    operation = @Operation(
                            operationId = "aprobarSolicitudPrestamo",
                            summary = "Aprobar solicitud de préstamo",
                            tags = {"Solicitud de Préstamo"},
                            security = { @SecurityRequirement(name = "bearerAuth") }

            )
    )
    })
    public RouterFunction<ServerResponse> solicitudPrestamoRoutes(SolicitudPrestamoHandler solicitudPrestamoHandler) {
        RouterFunction<ServerResponse> postRoute = route()
                .POST(ROUTE, solicitudPrestamoHandler::listenRegistrarSolicitud)
                .filter(securityFilter.requirePermission(PermissionEnum.SOLICITAR_PRESTAMO))
                .build();

        RouterFunction<ServerResponse> getRoute = route()
                .GET(ROUTE, solicitudPrestamoHandler::listenListarSolicitudesPendientes)
                .filter(securityFilter.requirePermission(PermissionEnum.LISTAR_SOLICITUDES_PENDIENTES))
                .build();

        RouterFunction<ServerResponse> putRoute = route()
                .PUT(ROUTE, solicitudPrestamoHandler::listenAprobarSolicitud)
                .filter(securityFilter.requirePermission(PermissionEnum.APROBAR_SOLICITUD))
                .build();

        return postRoute.and(getRoute).and(putRoute);
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
        return route()
                .GET("/api/health", r -> ServerResponse.ok().bodyValue("OK"))
                .build();
    }
}
