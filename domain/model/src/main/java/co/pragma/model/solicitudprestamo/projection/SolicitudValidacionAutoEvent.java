package co.pragma.model.solicitudprestamo.projection;

import co.pragma.model.cliente.projection.ClienteInfo;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class SolicitudValidacionAutoEvent {
    private SolicitudInfo solicitud;
    private ClienteInfo cliente;
    private List<PrestamoInfo> prestamosActivos;
}


