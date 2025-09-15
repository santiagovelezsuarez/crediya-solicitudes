package co.pragma.api.mapper;

import co.pragma.api.dto.request.AprobarSolicitudDTO;
import co.pragma.api.dto.request.SolicitarPrestamoDTO;
import co.pragma.api.dto.response.SolicitudPrestamoResponseDTO;
import co.pragma.model.estadosolicitud.EstadoSolicitudCodigo;
import co.pragma.model.solicitudprestamo.*;
import co.pragma.model.solicitudprestamo.command.AprobarSolicitudCommand;
import co.pragma.model.solicitudprestamo.command.SolicitarPrestamoCommand;
import org.springframework.stereotype.Component;

@Component
public class SolicitudPrestamoDtoMapper {

    public SolicitudPrestamoResponseDTO toResponse(SolicitudPrestamo solicitud) {
        return SolicitudPrestamoResponseDTO.builder()
                .id(String.valueOf(solicitud.getId()))
                .monto(solicitud.getMonto())
                .plazoEnMeses(solicitud.getPlazoEnMeses())
                .tipoPrestamo(solicitud.getIdTipoPrestamo() != null ? solicitud.getIdTipoPrestamo().toString() : null)
                .estado(solicitud.getEstado() != null ? solicitud.getEstado().name() : null)
                .build();
    }

    public SolicitarPrestamoCommand toCommand(SolicitarPrestamoDTO dto, String userId) {
        return SolicitarPrestamoCommand.builder()
                .idCliente(userId)
                .monto(dto.getMonto())
                .plazoEnMeses(dto.getPlazoEnMeses())
                .tipoPrestamo(dto.getTipoPrestamo())
                .build();
    }

    public static AprobarSolicitudCommand toAprobarCommand(AprobarSolicitudDTO dto) {
        return AprobarSolicitudCommand.builder()
                .codigoSolicitud(dto.getCodigoSolicitud())
                .estado(EstadoSolicitudCodigo.valueOf(dto.getDecisionFinal().toUpperCase()))
                .build();
    }
}
