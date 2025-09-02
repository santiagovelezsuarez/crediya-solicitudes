package co.pragma.api.dto;

import co.pragma.model.solicitudprestamo.SolicitudPrestamo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SolicitudPrestamoDtoMapper {

    @Mapping(target = "id", ignore = true)
    SolicitudPrestamo toModel(SolicitarPrestamoDTO request);

    SolicitudPrestamoResponse toResponse(SolicitudPrestamo solicitud);
}
