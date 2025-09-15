package co.pragma.model.solicitudprestamo;

import co.pragma.model.estadosolicitud.EstadoSolicitudCodigo;
import lombok.Builder;

@Builder
public record AprobarSolicitudCommand(
        String codigoSolicitud,
        EstadoSolicitudCodigo estado
) {}
