package co.pragma.api;

import co.pragma.api.adapter.ResponseService;
import co.pragma.api.dto.DtoValidator;
import co.pragma.api.dto.request.SolicitarPrestamoDTO;
import co.pragma.api.dto.response.SolicitudPrestamoResponseDTO;
import co.pragma.api.handler.SolicitudPrestamoHandler;
import co.pragma.api.mapper.SolicitudPrestamoDtoMapper;
import co.pragma.api.security.SecurityHandlerFilter;
import co.pragma.api.security.UserContextExtractor;
import co.pragma.model.solicitudprestamo.SolicitudPrestamo;
import co.pragma.model.solicitudprestamo.command.SolicitarPrestamoCommand;
import co.pragma.model.solicitudprestamo.projection.SolicitudPrestamoRevision;
import co.pragma.security.PermissionEnum;
import co.pragma.security.UserContextRequest;
import co.pragma.usecase.security.PermissionValidator;
import co.pragma.usecase.solicitud.AprobarSolicitudPrestamoUseCase;
import co.pragma.usecase.solicitud.ListarSolicitudesRevisionManualUseCase;
import co.pragma.usecase.solicitud.SolicitarPrestamoUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@WebFluxTest
@ContextConfiguration(classes = {RouterRest.class, SolicitudPrestamoHandler.class})
@Import({SecurityHandlerFilter.class})
class RouterRestTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private SolicitarPrestamoUseCase solicitarPrestamoUseCase;

    @MockitoBean
    private ListarSolicitudesRevisionManualUseCase listarSolicitudesRevisionManualUseCase;

    @MockitoBean
    private AprobarSolicitudPrestamoUseCase aprobarSolicitudPrestamoUseCase;

    @MockitoBean
    private SolicitudPrestamoDtoMapper solicitudPrestamoDtoMapper;

    @MockitoBean
    private PermissionValidator permissionValidator;

    @MockitoBean
    private UserContextExtractor userContextExtractor;

    @MockitoBean
    private DtoValidator dtoValidator;

    @MockitoBean
    private ResponseService responseService;

    private SolicitudPrestamo solicitud;
    private UserContextRequest userContext;

    @BeforeEach
    void setUp() {
        userContext = new UserContextRequest("admin-123", "admin@test.com", "ADMIN",
                Set.of(PermissionEnum.LISTAR_SOLICITUDES_PENDIENTES, PermissionEnum.SOLICITAR_PRESTAMO));
    }

    @Test
    void shouldListarSolicitudesPendientesSuccesfully() {
        SolicitudPrestamoRevision solicitudPrestamoRevision = SolicitudPrestamoRevision.builder()
                .id(UUID.randomUUID())
                .build();

        List<SolicitudPrestamoRevision> list = List.of(solicitudPrestamoRevision);

        when(userContextExtractor.fromRequest(any()))
                .thenReturn(userContext);
        when(permissionValidator.requirePermission(any(), eq(PermissionEnum.LISTAR_SOLICITUDES_PENDIENTES)))
                .thenReturn(Mono.empty());
        when(listarSolicitudesRevisionManualUseCase.execute(anyInt(), anyInt()))
                .thenReturn(Mono.just(list));
        when(responseService.okJson(any()))
                .thenAnswer(inv -> {
                    Object body = inv.getArgument(0);
                    return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(body);
                });

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/solicitud-prestamo")
                        .queryParam("page", "1")
                        .queryParam("size", "5")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(SolicitudPrestamoRevision.class)
                .consumeWith(result -> {
                    List<SolicitudPrestamoRevision> solicitudes = result.getResponseBody();
                    assertThat(solicitudes).isNotNull();
                    assertThat(solicitudes).hasSize(1);
                    assertThat(solicitudes.get(0).id()).isEqualTo(solicitudPrestamoRevision.id());
                });
    }

    @Test
    void shouldRegistrarSolicitudSuccessfully() {
        SolicitarPrestamoDTO dto = new SolicitarPrestamoDTO(
                BigDecimal.valueOf(2850000),
                12,
                "LIBRE_INVERSION"
        );

        solicitud = SolicitudPrestamo.builder()
                .id(UUID.randomUUID())
                .build();

        SolicitudPrestamoResponseDTO response = SolicitudPrestamoResponseDTO.builder()
                        .id(String.valueOf(solicitud.getId()))
                        .build();

        SolicitarPrestamoCommand cmd = SolicitarPrestamoCommand.builder()
                .idCliente(String.valueOf(UUID.randomUUID()))
                .monto(BigDecimal.valueOf(2850000))
                .plazoEnMeses(12)
                .build();

        when(userContextExtractor.fromRequest(any()))
                .thenReturn(userContext);
        when(permissionValidator.requirePermission(any(), eq(PermissionEnum.SOLICITAR_PRESTAMO)))
                .thenReturn(Mono.empty());
        when(dtoValidator.validate(any()))
                .thenReturn(Mono.just(dto));
        when(solicitudPrestamoDtoMapper.toCommand(any(), eq(userContext.userId())))
                .thenReturn(cmd);
        when(solicitarPrestamoUseCase.execute(any()))
                .thenReturn(Mono.just(solicitud));
        when(solicitudPrestamoDtoMapper.toResponse(any()))
                .thenReturn(response);
        when(responseService.createdJson(any()))
                .thenAnswer(inv -> {
                    Object body = inv.getArgument(0);
                    return ServerResponse.created(URI.create("/api/v1/solicitud-prestamo/" + solicitud.getId()))
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(body);
                });

        webTestClient.post()
                .uri("/api/v1/solicitud-prestamo")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(SolicitudPrestamoResponseDTO.class)
                .consumeWith(result -> {
                    SolicitudPrestamoResponseDTO body = result.getResponseBody();
                    assertThat(body).isNotNull();
                    assertThat(body.id()).isEqualTo(String.valueOf(solicitud.getId()));
                });
    }
}
