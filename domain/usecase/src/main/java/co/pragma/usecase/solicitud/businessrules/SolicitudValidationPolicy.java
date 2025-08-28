package co.pragma.usecase.solicitud.businessrules;

import co.pragma.base.gateways.BusinessValidator;
import co.pragma.model.solicitud.Solicitud;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public class SolicitudValidationPolicy implements BusinessValidator<Solicitud> {

    private List<BusinessValidator<Solicitud>> validators;

    public SolicitudValidationPolicy(List<BusinessValidator<Solicitud>> validators) {
        this.validators = validators;
    }

    @Override
    public Mono<Solicitud> validate(Solicitud solicitud) {
        return Flux.fromIterable(validators)
                .flatMap(validator -> validator.validate(solicitud))
                .then(Mono.just(solicitud));
    }
}
