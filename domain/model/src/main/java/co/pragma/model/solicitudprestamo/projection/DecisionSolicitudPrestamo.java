package co.pragma.model.solicitudprestamo.projection;

import co.pragma.model.estadosolicitud.EstadoSolicitudCodigo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DecisionSolicitudPrestamo {
    private String codigoSolicitud;
    private EstadoSolicitudCodigo decision;
}
