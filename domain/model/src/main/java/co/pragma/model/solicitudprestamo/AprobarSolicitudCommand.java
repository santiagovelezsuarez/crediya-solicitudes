package co.pragma.model.solicitudprestamo;

import co.pragma.model.estadosolicitud.EstadoSolicitudCodigo;
import lombok.Builder;

import java.util.UUID;

@Builder
public record AprobarSolicitudCommand(
        UUID id,
        EstadoSolicitudCodigo estado
) {}
