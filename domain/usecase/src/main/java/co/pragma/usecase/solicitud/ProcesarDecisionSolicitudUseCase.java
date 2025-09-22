package co.pragma.usecase.solicitud;

import co.pragma.exception.business.SolicitudAlreadyProcessedException;
import co.pragma.exception.business.SolicitudPrestamoNotFound;
import co.pragma.model.cliente.gateways.ClienteRepository;
import co.pragma.model.solicitudprestamo.SolicitudPrestamo;
import co.pragma.model.solicitudprestamo.gateways.SolicitudEvaluadaPublisher;
import co.pragma.model.solicitudprestamo.gateways.SolicitudPrestamoRepository;
import co.pragma.model.solicitudprestamo.projection.SolicitudEvaluadaEvent;
import co.pragma.model.solicitudprestamo.projection.DecisionSolicitudPrestamo;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import java.time.Duration;

@RequiredArgsConstructor
public class ProcesarDecisionSolicitudUseCase {

    private final SolicitudPrestamoRepository solicitudPrestamoRepository;
    private final ClienteRepository clienteRepository;
    private final SolicitudEvaluadaPublisher solicitudEvaluadaPublisher;

    public Mono<SolicitudPrestamo> execute(DecisionSolicitudPrestamo evento) {

        return solicitudPrestamoRepository.findByCodigo(evento.getCodigoSolicitud())
                .switchIfEmpty(Mono.error(new SolicitudPrestamoNotFound()))
                .flatMap(solicitud -> aplicarDecision(solicitud, evento))
                .flatMap(solicitudPrestamoRepository::save)
                .flatMap(this::notificarResultado);
    }

    private Mono<SolicitudPrestamo> aplicarDecision(SolicitudPrestamo solicitud, DecisionSolicitudPrestamo evento) {
        if (!solicitud.esProcesable())
            return Mono.error(new SolicitudAlreadyProcessedException(solicitud.getEstado().name()));

        solicitud.setEstado(evento.getDecision());
        solicitud.setNotificado(false);
        return Mono.just(solicitud);
    }

    private Mono<SolicitudPrestamo> notificarResultado(SolicitudPrestamo solicitud) {
        return publicarEventoNotificacion(solicitud)
                .then(solicitudPrestamoRepository.markAsNotificado(solicitud.getCodigo(), true))
                .thenReturn(solicitud)
                .onErrorResume(error -> Mono.just(solicitud));
    }

    private Mono<Void> publicarEventoNotificacion(SolicitudPrestamo solicitud) {
        // TODO: Cambiar a cliente.email() cuando SES salga de sandbox
        String emailCliente = "santiago.velezs@autonoma.edu.co";

        return clienteRepository.findById(solicitud.getIdCliente())
                .flatMap(cliente -> {
                    var event = SolicitudEvaluadaEvent.builder()
                            .codigoSolicitud(solicitud.getCodigo())
                            .emailCliente(emailCliente) // usar cliente.email() en prod
                            .nombreCliente(cliente.getFullName())
                            .monto(solicitud.getMonto())
                            .estado(solicitud.getEstado().name())
                            .tasaInteres(solicitud.getTasaInteres())
                            .plazoEnMeses(solicitud.getPlazoEnMeses())
                            .build();

                    return solicitudEvaluadaPublisher.publish(event)
                            .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                                    .maxBackoff(Duration.ofSeconds(10)))
                            .timeout(Duration.ofSeconds(15));
                });
    }
}

