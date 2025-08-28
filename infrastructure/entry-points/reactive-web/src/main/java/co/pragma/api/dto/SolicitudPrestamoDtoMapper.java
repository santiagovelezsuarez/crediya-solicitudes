package co.pragma.api.dto;

import co.pragma.model.solicitud.Solicitud;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SolicitudPrestamoDtoMapper {

    @Mapping(target = "id", ignore = true)
    Solicitud toModel(SolicitudPrestamoRequest request);

    SolicitudResponse toResponse(Solicitud solicitud);
}
