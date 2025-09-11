package co.pragma.api.dto;

import co.pragma.model.solicitudprestamo.SolicitarPrestamoCommand;
import co.pragma.model.solicitudprestamo.SolicitudPrestamo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SolicitudPrestamoDtoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "estado", ignore = true)
    SolicitudPrestamo toDomain(SolicitarPrestamoDTO dto);

    @Mapping(target = "estado", expression = "java(solicitud.getEstado().name())")
    @Mapping(target = "tipoPrestamo", ignore = true)
    SolicitudPrestamoResponseDTO toResponse(SolicitudPrestamo solicitud);

    default SolicitarPrestamoCommand toCommand(SolicitarPrestamoDTO dto, String userId) {
        return SolicitarPrestamoCommand.builder()
                .idCliente(userId)
                .monto(dto.getMonto())
                .plazoEnMeses(dto.getPlazoEnMeses())
                .tipoPrestamo(dto.getTipoPrestamo())
                .build();
    }

}
