package co.pragma.model.estadosolicitud;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class EstadoSolicitud {
    private Integer id;
    private EstadoSolicitudCodigoEnum nombre;
    private String descripcion;
}
