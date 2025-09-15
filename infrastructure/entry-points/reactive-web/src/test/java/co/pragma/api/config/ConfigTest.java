package co.pragma.api.config;

import co.pragma.api.adapters.ResponseService;
import co.pragma.api.handler.SolicitudPrestamoHandler;
import co.pragma.api.RouterRest;
import co.pragma.api.mapper.SolicitudPrestamoDtoMapper;
import co.pragma.api.security.JwtService;
import co.pragma.model.session.PermissionValidator;
import co.pragma.model.session.gateways.SessionProvider;
import co.pragma.model.estadosolicitud.gateways.EstadoSolicitudRepository;
import co.pragma.model.tipoprestamo.gateways.TipoPrestamoRepository;
import co.pragma.usecase.solicitud.ListarSolicitudesRevisionManualUseCase;
import co.pragma.usecase.solicitud.SolicitarPrestamoUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

@ContextConfiguration(classes = {RouterRest.class, SolicitudPrestamoHandler.class})
@WebFluxTest
@Import({SecurityConfig.class, CorsConfig.class, SecurityHeadersConfig.class})
class ConfigTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private SolicitarPrestamoUseCase solicitarPrestamoUseCase;

    @MockitoBean
    private ResponseService responseService;

    @MockitoBean
    private PermissionValidator permissionValidator;

    @MockitoBean
    private SessionProvider sessionProvider;

    @MockitoBean
    private ListarSolicitudesRevisionManualUseCase listarSolicitudesRevisionManualUseCase;

    @MockitoBean
    private SolicitudPrestamoDtoMapper solicitudPrestamoDtoMapper;

    @MockitoBean
    private TipoPrestamoRepository tipoPrestamoRepository;

    @MockitoBean
    private EstadoSolicitudRepository estadoSolicitudRepository;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void corsConfigurationShouldAllowOrigins() {
        webTestClient.get()
                .uri("/api/health")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Security-Policy", "default-src 'self'; frame-ancestors 'self'; form-action 'self'")
                .expectHeader().valueEquals("Strict-Transport-Security", "max-age=31536000;")
                .expectHeader().valueEquals("X-Content-Type-Options", "nosniff")
                .expectHeader().valueEquals("Server", "")
                .expectHeader().valueEquals("Cache-Control", "no-store")
                .expectHeader().valueEquals("Pragma", "no-cache")
                .expectHeader().valueEquals("Referrer-Policy", "strict-origin-when-cross-origin");
    }

}