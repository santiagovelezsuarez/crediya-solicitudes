package co.pragma.model.tipoprestamo;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TipoPrestamo {
    private UUID id;
    private String nombre;
    private BigDecimal montoMinimo;
    private BigDecimal montoMaximo;
    private BigDecimal tasaInteres;
    private boolean validacionAutomatica;
}
