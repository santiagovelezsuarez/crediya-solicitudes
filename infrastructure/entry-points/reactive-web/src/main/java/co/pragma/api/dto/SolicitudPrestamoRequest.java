package co.pragma.api.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Positive;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudPrestamoRequest {
    @Pattern(regexp = RegexConstants.UUID_REGEX, message = "El Identificador de usuario no está bien formado")
    private String idUsuario;

    @Positive(message = "El monto debe ser positivo")
    private double monto;

    @Positive(message = "El plazo en meses debe ser positivo")
    private int plazoEnMeses;

    @Pattern(regexp = RegexConstants.UUID_REGEX, message = "El Identificador de tipo de préstamo no está bien formado")
    private String idTipoPrestamo;
}
