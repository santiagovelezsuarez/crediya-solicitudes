package co.pragma.usecase.solicitud.businessrules;

import co.pragma.exception.business.MontoPrestamoOutOfRangeException;
import co.pragma.exception.business.TipoPrestamoNotFoundException;
import co.pragma.model.solicitudprestamo.command.SolicitarPrestamoCommand;
import co.pragma.model.tipoprestamo.TipoPrestamo;
import co.pragma.model.tipoprestamo.gateways.TipoPrestamoRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class TipoPrestamoValidator {

    private final TipoPrestamoRepository tipoPrestamoRepository;

    public Mono<Void> validate(SolicitarPrestamoCommand cmd) {
        BigDecimal montoPrestamo = cmd.monto();

        return tipoPrestamoRepository.findByNombre(cmd.tipoPrestamo())
                .switchIfEmpty(Mono.error(new TipoPrestamoNotFoundException()))
                .flatMap(tipo -> validateMontoRange(montoPrestamo, tipo))
                .then();
    }

    private Mono<Void> validateMontoRange(BigDecimal montoPrestamo, TipoPrestamo tipo) {
        BigDecimal montoMinimo = tipo.getMontoMinimo();
        BigDecimal montoMaximo = tipo.getMontoMaximo();

        if (!isWithinRange(montoPrestamo, montoMinimo, montoMaximo))
            return Mono.error(new MontoPrestamoOutOfRangeException(montoPrestamo, montoMinimo, montoMaximo));

        return Mono.empty();
    }

    private boolean isWithinRange(BigDecimal monto, BigDecimal minimo, BigDecimal maximo) {
        return monto.compareTo(minimo) >= 0 && monto.compareTo(maximo) <= 0;
    }
}
