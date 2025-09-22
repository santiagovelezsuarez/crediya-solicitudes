package co.pragma.model.solicitudprestamo.command;

import co.pragma.model.estadosolicitud.EstadoSolicitudCodigoEnum;
import lombok.Builder;

@Builder
public record AprobarSolicitudCommand(
        String codigoSolicitud,
        EstadoSolicitudCodigoEnum estado
) {}
