package co.pragma.usecase.solicitud;

import co.pragma.exception.business.SolicitudAlreadyProcessedException;
import co.pragma.exception.business.SolicitudPrestamoNotFound;
import co.pragma.model.cliente.gateways.ClienteRepository;
import co.pragma.model.solicitudprestamo.command.AprobarSolicitudCommand;
import co.pragma.model.solicitudprestamo.SolicitudPrestamo;
import co.pragma.model.solicitudprestamo.gateways.ResultadoSolicitudPublisher;
import co.pragma.model.solicitudprestamo.gateways.SolicitudPrestamoRepository;
import co.pragma.model.solicitudprestamo.projection.EstadoSolicitudEvent;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import java.time.Duration;

@RequiredArgsConstructor
public class AprobarSolicitudPrestamoUseCase {

    private final SolicitudPrestamoRepository solicitudPrestamoRepository;
    private final ClienteRepository clienteRepository;
    private final SolicitudPrestamoEventPublisher solicitudEventPublisher;

    public Mono<SolicitudPrestamo> execute(AprobarSolicitudCommand cmd) {
        return solicitudPrestamoRepository.findByCodigo(cmd.codigoSolicitud())
                .switchIfEmpty(Mono.error(new SolicitudPrestamoNotFound()))
                .flatMap(solicitud -> procesarAprobacion(solicitud, cmd))
                .flatMap(solicitudPrestamoRepository::save)
                .flatMap(this::intentarPublicarEvento);
    }

    private Mono<SolicitudPrestamo> procesarAprobacion(SolicitudPrestamo solicitud, AprobarSolicitudCommand cmd) {
        if (!solicitud.esProcesable())
            return Mono.error(new SolicitudAlreadyProcessedException(solicitud.getEstado().name()));

        solicitud.setEstado(cmd.estado());
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
        // TODO: Cambiar a cliente.email() una vez la cuenta de SES salga de sandbox
        String emailCliente = "santiago.velezs@autonoma.edu.co"; /* AWS Sandbox verified email */
        return clienteRepository.findById(solicitud.getIdCliente())
                .flatMap(cliente -> {
                    var event = EstadoSolicitudEvent.builder()
                            .codigoSolicitud(solicitud.getCodigo())
                            /* Ambiente Desarrollo con email verificado en AWS */
                            //.emailCliente(cliente.email())
                            .emailCliente(emailCliente)
                            .nombreCliente(cliente.getFullName())
                            .monto(solicitud.getMonto())
                            .estado(solicitud.getEstado().name())
                            .tasaInteres(solicitud.getTasaInteres())
                            .build();

                    return solicitudEventPublisher.publish(event)
                            .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                                    .maxBackoff(Duration.ofSeconds(10)))
                            .timeout(Duration.ofSeconds(15));
                });
    }
}
