package co.pragma.model.solicitudprestamo;

import lombok.Builder;
import java.math.BigDecimal;

@Builder
public record SolicitarPrestamoCommand (
        String idCliente,
        BigDecimal monto,
        Integer plazoEnMeses,
        String tipoPrestamo
) {}
