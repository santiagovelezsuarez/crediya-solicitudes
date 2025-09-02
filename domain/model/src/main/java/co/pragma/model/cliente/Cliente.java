package co.pragma.model.cliente;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Cliente {
    private UUID id;
    private DocumentoIdentidadVO documentoIdentidad;
    private String email;
}
