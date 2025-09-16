package co.pragma.model.cliente;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Cliente {
    private UUID id;
    private String nombres;
    private String apellidos;
    private String email;
    private BigDecimal salarioBase;

    public String getFullName() {
        return nombres + " " + apellidos;
    }
}
