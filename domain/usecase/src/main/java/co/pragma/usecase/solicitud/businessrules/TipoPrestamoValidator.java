package co.pragma.usecase.solicitud.businessrules;

import co.pragma.model.solicitudprestamo.SolicitarPrestamoCommand;
import co.pragma.model.tipoprestamo.gateways.TipoPrestamoRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class TipoPrestamoValidator {

    private final TipoPrestamoRepository tipoPrestamoRepository;

    public Mono<Void> validate(SolicitarPrestamoCommand cmd) {
        /** TODO
         * VALIDATE Monto MIN *
         * VALIDATE Monto MAX *
         **/
        return Mono.empty();
    }
}
