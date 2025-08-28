package co.pragma.usecase.solicitud;

import co.pragma.base.gateways.BusinessValidator;
import co.pragma.model.solicitud.Solicitud;
import co.pragma.model.solicitud.gateways.SolicitudRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class SolicitudUseCase {

    private static final int DEFAULT_ESTADO_ID = 1;
    private final SolicitudRepository solicitudRepository;
    private final BusinessValidator<Solicitud> registrationValidator;

    public Mono<Solicitud> createSolicitud(Solicitud solicitud) {
        return  Mono.just(solicitud)
                .doOnNext(this::setDefaultValues)
                .flatMap(registrationValidator::validate)
                .flatMap(solicitudRepository::save);
    }

    private void setDefaultValues(Solicitud solicitud) {
        if (solicitud.getIdEstado() == null) {
            solicitud.setIdEstado(DEFAULT_ESTADO_ID);
        }
    }

}
