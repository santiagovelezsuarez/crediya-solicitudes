package co.pragma.model.solicitudprestamo.projection;

import co.pragma.model.estadosolicitud.EstadoSolicitudCodigo;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record  SolicitudPrestamoRevision(
        UUID id,
        BigDecimal monto,
        Integer plazoEnMeses,
        String emailCliente,
        String nombreCliente,
        String tipoPrestamo,
        BigDecimal tasaInteres,
        EstadoSolicitudCodigo estado,
        BigDecimal salarioBase,
        BigDecimal montoMensualSolicitud
){}
