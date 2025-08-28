package co.pragma.api.dto;

import java.math.BigDecimal;

public record SolicitudResponse(
    String id,
    String usuarioId,
    BigDecimal monto,
    int plazoEnMeses,
    String idTipoPrestamo,
    String idEstado
) {
}
