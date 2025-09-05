package co.pragma.api.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                title = "API Microservicio de Solicitudes de Préstamo",
                version = "v1",
                description = "Documentación de los endpoints de gestión de solicitudes de préstamo"
        ),
        servers = {
                @Server(url = "http://localhost:8081", description = "Servidor local")
        }
)
public class OpenApiConfig {
}
