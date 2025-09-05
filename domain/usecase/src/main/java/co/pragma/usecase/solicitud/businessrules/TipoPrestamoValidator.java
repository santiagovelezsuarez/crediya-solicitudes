package co.pragma.usecase.solicitud.businessrules;

import co.pragma.exception.TipoPrestamoNotFoundException;
import co.pragma.gateways.BusinessValidator;
import co.pragma.model.tipoprestamo.TipoPrestamo;
import co.pragma.model.tipoprestamo.TipoPrestamoVO;
import co.pragma.model.tipoprestamo.gateways.TipoPrestamoRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class TipoPrestamoValidator implements BusinessValidator<TipoPrestamoVO, TipoPrestamo> {

    private final TipoPrestamoRepository tipoPrestamoRepository;

    @Override
    public Mono<TipoPrestamo> validate(TipoPrestamoVO tipoPrestamo) {

        return tipoPrestamoRepository.findByNombre(tipoPrestamo.nombre())
                .switchIfEmpty(Mono.error(new TipoPrestamoNotFoundException("Tipo de préstamo '" + tipoPrestamo.nombre() + "' no existe")));

    }
}
