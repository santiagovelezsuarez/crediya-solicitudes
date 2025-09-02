package co.pragma.api.dto;

import java.math.BigDecimal;

public record SolicitudPrestamoResponse(
    String id,
    BigDecimal monto,
    int plazoEnMeses,
    String tipoPrestamo,
    String estado
) {
}
