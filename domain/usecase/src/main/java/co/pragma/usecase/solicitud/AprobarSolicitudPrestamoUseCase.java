package co.pragma.usecase.solicitud;

import co.pragma.model.solicitudprestamo.command.AprobarSolicitudCommand;
import co.pragma.model.solicitudprestamo.SolicitudPrestamo;
import co.pragma.model.solicitudprestamo.projection.DecisionSolicitudPrestamo;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class AprobarSolicitudPrestamoUseCase {

    private final ProcesarDecisionSolicitudUseCase procesarDecisionSolicitudUseCase;

    public Mono<SolicitudPrestamo> execute(AprobarSolicitudCommand cmd) {
        DecisionSolicitudPrestamo decisionSolicitudPrestamo = DecisionSolicitudPrestamo.builder()
                .codigoSolicitud(cmd.codigoSolicitud())
                .decision(cmd.estado())
                .build();
        return procesarDecisionSolicitudUseCase.execute(decisionSolicitudPrestamo);
    }
}
