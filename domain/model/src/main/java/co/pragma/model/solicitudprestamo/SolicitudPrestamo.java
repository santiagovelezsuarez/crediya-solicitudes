package co.pragma.model.solicitudprestamo;
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
    private Integer idEstado;
    private BigDecimal monto;
    private Integer plazoEnMeses;
    private UUID idTipoPrestamo;
}
