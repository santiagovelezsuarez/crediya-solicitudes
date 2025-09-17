package co.pragma.model.solicitudprestamo.projection;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record EstadoSolicitudEvent(
        String codigoSolicitud,
        String emailCliente,
        String nombreCliente,
        BigDecimal monto,
        String estado,
        BigDecimal tasaInteres,
        Integer plazoEnMeses
) {}
