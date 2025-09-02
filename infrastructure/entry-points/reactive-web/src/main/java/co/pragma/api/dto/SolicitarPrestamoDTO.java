package co.pragma.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class SolicitarPrestamoDTO {

    @NotBlank(message = "El tipo de documento es obligatorio")
    @Pattern(regexp = "^(CC|CE|NIT)$", message = "El tipo de documento no es válido")
    private String tipoDocumento;

    @NotBlank(message = "El número de documento es obligatorio")
    @Pattern(regexp = "^\\d{5,15}$", message = "El número de documento no es válido")
    private String numeroDocumento;

    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser positivo")
    private BigDecimal monto;

    @NotNull(message = "El plazo en meses es obligatorio")
    @Positive(message = "El plazo en meses debe ser positivo")
    private Integer plazoEnMeses;

    @NotBlank(message = "El tipo de préstamo es obligatorio")
    private String tipoPrestamo;
}
