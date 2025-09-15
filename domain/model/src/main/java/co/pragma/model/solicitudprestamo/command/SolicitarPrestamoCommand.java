package co.pragma.model.solicitudprestamo.command;

import lombok.Builder;
import java.math.BigDecimal;

@Builder
public record SolicitarPrestamoCommand (
        String idCliente,
        BigDecimal monto,
        Integer plazoEnMeses,
        String tipoPrestamo
) {}
