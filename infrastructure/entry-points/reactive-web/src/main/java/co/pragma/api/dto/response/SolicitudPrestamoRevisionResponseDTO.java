package co.pragma.api.dto.response;

import lombok.Builder;
import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record SolicitudPrestamoRevisionResponseDTO (
        UUID id,
        BigDecimal monto,
        Integer plazoEnMeses,
        String email,
        String nombre,
        String tipoPrestamo,
        BigDecimal tasaInteres,
        String estado,
        BigDecimal salarioBase,
        BigDecimal montoMensualSolicitud
) {}
