package co.pragma.api.mapper;

import co.pragma.api.dto.request.AprobarSolicitudDTO;
import co.pragma.api.dto.request.SolicitarPrestamoDTO;
import co.pragma.api.dto.response.SolicitudPrestamoResponseDTO;
import co.pragma.model.estadosolicitud.EstadoSolicitudCodigo;
import co.pragma.model.solicitudprestamo.*;
import co.pragma.model.solicitudprestamo.command.AprobarSolicitudCommand;
import co.pragma.model.solicitudprestamo.command.SolicitarPrestamoCommand;

public class SolicitudPrestamoDtoMapper {

    private SolicitudPrestamoDtoMapper() {}

    public static SolicitudPrestamoResponseDTO toResponse(SolicitudPrestamo solicitud) {
        return SolicitudPrestamoResponseDTO.builder()
                .id(String.valueOf(solicitud.getId()))
                .monto(solicitud.getMonto())
                .plazoEnMeses(solicitud.getPlazoEnMeses())
                .tipoPrestamo(solicitud.getIdTipoPrestamo() != null ? solicitud.getIdTipoPrestamo().toString() : null)
                .estado(solicitud.getEstado() != null ? solicitud.getEstado().name() : null)
                .build();
    }

    public static SolicitarPrestamoCommand toCommand(SolicitarPrestamoDTO dto, String userId) {
        return SolicitarPrestamoCommand.builder()
                .monto(dto.getMonto())
                .tipoPrestamo(dto.getTipoPrestamo())
                .plazoEnMeses(dto.getPlazoEnMeses())
                .idCliente(userId)
                .build();
    }

    public static AprobarSolicitudCommand toAprobarCommand(AprobarSolicitudDTO dto) {
        return AprobarSolicitudCommand.builder()
                .codigoSolicitud(dto.getCodigoSolicitud())
                .estado(EstadoSolicitudCodigo.valueOf(dto.getDecisionFinal().toUpperCase()))
                .build();
    }
}
