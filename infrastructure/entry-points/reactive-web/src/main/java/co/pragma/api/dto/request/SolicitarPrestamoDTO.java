package co.pragma.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class SolicitarPrestamoDTO {

    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser positivo")
    private BigDecimal monto;

    @NotNull(message = "El plazo en meses es obligatorio")
    @Positive(message = "El plazo en meses debe ser positivo")
    private Integer plazoEnMeses;

    @NotBlank(message = "El tipo de préstamo es obligatorio")
    private String tipoPrestamo;
}
