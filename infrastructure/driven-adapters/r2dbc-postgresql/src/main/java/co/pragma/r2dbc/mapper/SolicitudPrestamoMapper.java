package co.pragma.r2dbc.mapper;

import co.pragma.model.estadosolicitud.EstadoSolicitudCodigo;
import co.pragma.model.solicitudprestamo.SolicitudPrestamo;
import co.pragma.r2dbc.entity.SolicitudPrestamoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SolicitudPrestamoMapper {

    @Mapping(target = "estado", source = "idEstado")
    SolicitudPrestamo toModel(SolicitudPrestamoEntity entity);

    @Mapping(target = "idEstado", source = "estado")
    SolicitudPrestamoEntity toEntity(SolicitudPrestamo model);

    default EstadoSolicitudCodigo map(short idEstado) {
        return EstadoSolicitudCodigo.valueOf(idEstado);
    }

    default short map(EstadoSolicitudCodigo estado) {
        return estado.getCode();
    }
}

