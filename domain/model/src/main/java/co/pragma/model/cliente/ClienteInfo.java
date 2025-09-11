package co.pragma.model.cliente;

import lombok.Builder;
import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record ClienteInfo(
        UUID id,
        String email,
        String nombre,
        BigDecimal salarioBase,
        BigDecimal bigDecimal) {}
