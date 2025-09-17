package co.pragma.usecase.solicitud;

import co.pragma.exception.business.SolicitudAlreadyProcessedException;
import co.pragma.exception.business.SolicitudPrestamoNotFound;
import co.pragma.model.cliente.gateways.ClienteRepository;
import co.pragma.model.solicitudprestamo.SolicitudPrestamo;
import co.pragma.model.solicitudprestamo.gateways.ResultadoSolicitudPublisher;
import co.pragma.model.solicitudprestamo.gateways.SolicitudPrestamoRepository;
import co.pragma.model.solicitudprestamo.projection.EstadoSolicitudEvent;
import co.pragma.model.solicitudprestamo.projection.DecisionSolicitudPrestamo;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import java.time.Duration;

@RequiredArgsConstructor
public class ActualizarEstadoSolicitudUseCase {

    private final SolicitudPrestamoRepository solicitudPrestamoRepository;
    private final ClienteRepository clienteRepository;
    private final ResultadoSolicitudPublisher resultadoSolicitudPublisher;

    public Mono<SolicitudPrestamo> execute(DecisionSolicitudPrestamo evento) {
        //{"codigo":"SP-20250917-A4E0F722","decisionFinal":"REVISION_MANUAL"}
        return solicitudPrestamoRepository.findByCodigo(evento.getCodigoSolicitud())
                .doOnSubscribe(sub -> System.out.println("Buscando solicitud con código: " + evento.getCodigoSolicitud()))
                .switchIfEmpty(Mono.error(new SolicitudPrestamoNotFound()))
                .flatMap(solicitud -> procesarActualizacion(solicitud, evento))
                .flatMap(solicitudPrestamoRepository::save)
                .flatMap(this::intentarPublicarEvento);
    }

    private Mono<SolicitudPrestamo> procesarActualizacion(SolicitudPrestamo solicitud, DecisionSolicitudPrestamo evento) {
        if (!solicitud.esProcesable())
            return Mono.error(new SolicitudAlreadyProcessedException(solicitud.getEstado().name()));

        solicitud.setEstado(evento.getDecision());
        solicitud.setNotificado(false);
        return Mono.just(solicitud);
    }

    private Mono<SolicitudPrestamo> intentarPublicarEvento(SolicitudPrestamo solicitud) {
        return publicarEvento(solicitud)
                .then(solicitudPrestamoRepository.markAsNotificado(solicitud.getCodigo(), true))
                .thenReturn(solicitud)
                .onErrorResume(error -> Mono.just(solicitud));
    }

    private Mono<Void> publicarEvento(SolicitudPrestamo solicitud) {
        // TODO: Cambiar a cliente.email() cuando SES salga de sandbox
        String emailCliente = "santiago.velezs@autonoma.edu.co";

        return clienteRepository.findById(solicitud.getIdCliente())
                .flatMap(cliente -> {
                    var event = EstadoSolicitudEvent.builder()
                            .codigoSolicitud(solicitud.getCodigo())
                            .emailCliente(emailCliente) // usar cliente.email() en prod
                            .nombreCliente(cliente.getFullName())
                            .monto(solicitud.getMonto())
                            .estado(solicitud.getEstado().name())
                            .tasaInteres(solicitud.getTasaInteres())
                            .plazoEnMeses(solicitud.getPlazoEnMeses())
                            .build();

                    return resultadoSolicitudPublisher.publish(event)
                            .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                                    .maxBackoff(Duration.ofSeconds(10)))
                            .timeout(Duration.ofSeconds(15));
                });
    }
}

