package co.pragma.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AprobarSolicitudDTO {

    @NotNull(message = "El id de la solicitud es obligatorio")
    private UUID idSolicitud;

    @NotBlank(message = "El campo decisionFinal no puede estar vacío")
    @Pattern(regexp = "APROBADA|RECHAZADA", message = "Debe APROBAR/RECHAZAR esta solicitud.")
    private String decisionFinal;
}
