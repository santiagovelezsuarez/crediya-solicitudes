package co.pragma.api.dto;

import co.pragma.model.solicitudprestamo.SolicitudPrestamo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SolicitudPrestamoDtoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "idCliente", ignore = true)
    @Mapping(target = "idTipoPrestamo", ignore = true)
    @Mapping(target = "estado", ignore = true)
    SolicitudPrestamo toModel(SolicitarPrestamoDTO request);

    @Mapping(target = "estado", expression = "java(solicitud.getEstado().name())")
    @Mapping(target = "tipoPrestamo", ignore = true)
    SolicitudPrestamoResponse toResponse(SolicitudPrestamo solicitud);


}
