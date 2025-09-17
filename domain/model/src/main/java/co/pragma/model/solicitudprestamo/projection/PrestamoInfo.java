package co.pragma.model.solicitudprestamo.projection;

import lombok.Builder;
import java.math.BigDecimal;

@Builder
public record PrestamoInfo(
    BigDecimal monto,
    Integer plazoEnMeses,
    BigDecimal tasaInteresAnual
) {}
