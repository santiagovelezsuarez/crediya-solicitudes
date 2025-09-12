package co.pragma.usecase.solicitud;

import co.pragma.exception.business.SolicitudAlreadyProcessedException;
import co.pragma.exception.business.SolicitudPrestamoNotFound;
import co.pragma.model.solicitudprestamo.AprobarSolicitudCommand;
import co.pragma.model.solicitudprestamo.SolicitudPrestamo;
import co.pragma.model.solicitudprestamo.gateways.SolicitudPrestamoEventPublisher;
import co.pragma.model.solicitudprestamo.gateways.SolicitudPrestamoRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@RequiredArgsConstructor
public class AprobarSolicitudPrestamoUseCase {

    private final SolicitudPrestamoRepository solicitudPrestamoRepository;
    private final SolicitudPrestamoEventPublisher solicitudEventPublisher;

    public Mono<SolicitudPrestamo> execute(AprobarSolicitudCommand cmd) {
        return solicitudPrestamoRepository.findById(cmd.id())
                .switchIfEmpty(Mono.error(new SolicitudPrestamoNotFound()))
                .flatMap(solicitud -> procesarAprobacion(solicitud, cmd))
                .flatMap(solicitudPrestamoRepository::save)
                .flatMap(this::publicarEvento);
    }

    private Mono<SolicitudPrestamo> procesarAprobacion(SolicitudPrestamo solicitud, AprobarSolicitudCommand cmd) {
        if (!solicitud.esProcesable())
            return Mono.error(new SolicitudAlreadyProcessedException(solicitud.getEstado().name()));

        solicitud.setEstado(cmd.estado());
        return Mono.just(solicitud);
    }

    private Mono<SolicitudPrestamo> publicarEvento(SolicitudPrestamo solicitud) {
        return solicitudEventPublisher.publishEstadoActualizado(solicitud)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                                .maxBackoff(Duration.ofSeconds(10))
                                .filter(ex -> !(ex instanceof SolicitudAlreadyProcessedException))
                )
                .doOnSuccess(v -> System.out.println("Evento publicado en SQS para solicitud: " + solicitud.getId()))
                .doOnError(ex -> System.err.println("Error publicando evento en SQS para solicitud: "+ solicitud.getId() + " - " + ex.getMessage()))
                .thenReturn(solicitud);
    }
}
