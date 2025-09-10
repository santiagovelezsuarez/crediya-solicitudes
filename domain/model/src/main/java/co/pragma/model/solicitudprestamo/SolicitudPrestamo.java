package co.pragma.model.solicitudprestamo;
import co.pragma.model.cliente.Cliente;
import co.pragma.model.estadosolicitud.EstadoSolicitudCodigo;
import co.pragma.model.tipoprestamo.TipoPrestamo;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class SolicitudPrestamo {
    private UUID id;
    private Cliente cliente;
    private TipoPrestamo tipoPrestamo;
    private BigDecimal monto;
    private Integer plazoEnMeses;
    private EstadoSolicitudCodigo estado;
}
