package co.pragma.model.solicitud;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Solicitud {
    private String id;
    private String usuarioId;
    private String estado;
    private BigDecimal monto;
    private int plazoEnMeses;
    private String tipoPrestamo;
}
