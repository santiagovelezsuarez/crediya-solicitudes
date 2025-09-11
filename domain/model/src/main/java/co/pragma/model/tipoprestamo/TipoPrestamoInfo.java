package co.pragma.model.tipoprestamo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoPrestamoInfo {
    private UUID id;
    private String nombre;
    private BigDecimal tasaInteres;
}
