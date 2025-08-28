package co.pragma.model.solicitud;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
public class Solicitud {
    private UUID id;
    private UUID idUsuario;
    private Integer idEstado;
    private BigDecimal monto;
    private Integer plazoEnMeses;
    private UUID idTipoPrestamo;
}
