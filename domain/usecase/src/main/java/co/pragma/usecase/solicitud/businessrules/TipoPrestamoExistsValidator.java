package co.pragma.usecase.solicitud.businessrules;

import co.pragma.base.exception.TipoPrestamoNotExistsException;
import co.pragma.base.gateways.BusinessValidator;
import co.pragma.model.solicitud.Solicitud;
import co.pragma.model.tipoprestamo.gateways.TipoPrestamoRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class TipoPrestamoExistsValidator implements BusinessValidator<Solicitud> {

    private final TipoPrestamoRepository tipoPrestamoRepository;

    @Override
    public Mono<Solicitud> validate(Solicitud solicitud) {
        return tipoPrestamoRepository.findById(solicitud.getIdTipoPrestamo())
                .switchIfEmpty(Mono.error(new TipoPrestamoNotExistsException("El tipo de préstamo no existe")))
                .thenReturn(solicitud);
    }
}
