package co.pragma.model.solicitudprestamo.projection;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class SolicitudInfo {
    private UUID id;
    private String codigo;
    private BigDecimal monto;
    private Integer plazoEnMeses;
    private UUID tipoPrestamoId;
    private BigDecimal tasaInteresAnual;
}
