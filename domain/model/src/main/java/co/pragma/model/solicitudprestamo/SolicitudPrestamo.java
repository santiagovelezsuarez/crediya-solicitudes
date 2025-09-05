package co.pragma.model.solicitudprestamo;
import co.pragma.model.estadosolicitud.EstadoSolicitudCodigo;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class SolicitudPrestamo {
    private UUID id;
    private UUID idCliente;
    private UUID idTipoPrestamo;
    private BigDecimal monto;
    private Integer plazoEnMeses;
    private EstadoSolicitudCodigo estado;
}
