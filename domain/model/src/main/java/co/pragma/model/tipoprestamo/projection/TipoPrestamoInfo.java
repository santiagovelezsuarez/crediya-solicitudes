package co.pragma.model.tipoprestamo.projection;

import lombok.Builder;
import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record TipoPrestamoInfo(
        UUID id,
        String nombre,
        BigDecimal tasaInteres
){}
