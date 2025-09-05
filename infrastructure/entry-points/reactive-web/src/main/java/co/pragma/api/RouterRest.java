package co.pragma.api;

import co.pragma.api.dto.ErrorResponse;
import co.pragma.api.dto.SolicitarPrestamoDTO;
import co.pragma.api.dto.SolicitudPrestamoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;

@Configuration
public class RouterRest {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/solicitud-prestamo",
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.POST,
                    beanClass = Handler.class,
                    beanMethod = "listenCreateSolicitud",
                    operation = @Operation(
                            operationId = "crearSolicitudPrestamo",
                            summary = "Crear una nueva solicitud de préstamo",
                            description = "Crea una solicitud de préstamo validando el cliente y el tipo de préstamo",
                            tags = {"Solicitud de Préstamo"},
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = SolicitarPrestamoDTO.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Solicitud creada exitosamente",
                                            content = @Content(schema = @Schema(implementation = SolicitudPrestamoResponse.class))),
                                    @ApiResponse(responseCode = "400", description = "Request inválido",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                                    @ApiResponse(responseCode = "404", description = "Cliente o tipo de préstamo no encontrado",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                                    @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> solicitudPrestamoRoutes(Handler handler) {
        return route(POST("/api/v1/solicitud-prestamo"), handler::listenCreateSolicitud);
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
