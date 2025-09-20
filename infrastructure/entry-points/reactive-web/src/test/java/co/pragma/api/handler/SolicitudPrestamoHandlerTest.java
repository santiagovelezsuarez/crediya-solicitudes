package co.pragma.api.handler;

import co.pragma.api.adapters.ResponseService;
import co.pragma.api.dto.DtoValidator;
import co.pragma.api.mapper.SolicitudPrestamoDtoMapper;
import co.pragma.model.solicitudprestamo.SolicitudPrestamo;
import co.pragma.model.solicitudprestamo.projection.SolicitudPrestamoRevision;
import co.pragma.usecase.security.PermissionValidator;
import co.pragma.usecase.solicitud.AprobarSolicitudPrestamoUseCase;
import co.pragma.usecase.solicitud.ListarSolicitudesRevisionManualUseCase;
import co.pragma.usecase.solicitud.SolicitarPrestamoUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SolicitudPrestamoHandlerTest {

    @Mock
    private ListarSolicitudesRevisionManualUseCase listarSolicitudesUseCase;

    @Mock
    private SolicitarPrestamoUseCase solicitarPrestamoUseCase;

    @Mock
    private AprobarSolicitudPrestamoUseCase aprobarSolicitudPrestamoUseCase;

    @Spy
    private ResponseService responseService;

    @Mock
    private SolicitudPrestamoDtoMapper mapper;

    @Mock
    private PermissionValidator permissionValidator;

    @Mock
    private DtoValidator dtoValidator;

    @Mock
    private ServerRequest serverRequest;

    @InjectMocks
    private SolicitudPrestamoHandler handler;

    @Test
    void shouldListarSolicitudesPendientes() {
        SolicitudPrestamoRevision solicitud = SolicitudPrestamoRevision.builder().build();
        when(serverRequest.queryParam("page")).thenReturn(Optional.of("1"));
        when(serverRequest.queryParam("size")).thenReturn(Optional.of("5"));
        when(listarSolicitudesUseCase.execute(1, 5)).thenReturn(Mono.just(List.of(solicitud)));

        Mono<ServerResponse> responseMono = handler.listenListarSolicitudesPendientes(serverRequest);

        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    assertThat(response.statusCode()).isEqualTo(HttpStatus.OK);
                })
                .verifyComplete();

        verify(listarSolicitudesUseCase).execute(1, 5);
    }
}
