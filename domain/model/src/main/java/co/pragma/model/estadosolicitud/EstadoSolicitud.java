package co.pragma.model.estadosolicitud;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class EstadoSolicitud {
    private int id;
    private EstadoSolicitudCodigo nombre;
    private String descripcion;
}
