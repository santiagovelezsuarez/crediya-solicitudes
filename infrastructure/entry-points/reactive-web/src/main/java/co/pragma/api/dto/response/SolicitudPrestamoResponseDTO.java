package co.pragma.api.dto.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record SolicitudPrestamoResponseDTO(
    String id,
    BigDecimal monto,
    int plazoEnMeses,
    String tipoPrestamo,
    String estado
) {
}
