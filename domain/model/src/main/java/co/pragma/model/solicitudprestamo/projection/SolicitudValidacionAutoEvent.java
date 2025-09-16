package co.pragma.model.solicitudprestamo.projection;

import co.pragma.model.cliente.Cliente;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class SolicitudValidacionAutoEvent {
    private SolicitudInfo solicitud;
    private Cliente cliente;
    private List<PrestamoInfo> prestamosActivos;
}


