package co.pragma.r2dbc.mapper;

import co.pragma.model.cliente.Cliente;
import co.pragma.model.estadosolicitud.EstadoSolicitudCodigo;
import co.pragma.model.solicitudprestamo.SolicitudPrestamo;
import co.pragma.r2dbc.entity.SolicitudPrestamoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface SolicitudPrestamoMapper {

    @Mapping(target = "estado", source = "idEstado")
    SolicitudPrestamo toDomain(SolicitudPrestamoEntity entity);

    @Mapping(target = "idEstado", source = "estado")
    SolicitudPrestamoEntity toEntity(SolicitudPrestamo model);

    default EstadoSolicitudCodigo map(short idEstado) {
        return EstadoSolicitudCodigo.valueOf(idEstado);
    }

    default short map(EstadoSolicitudCodigo estado) {
        return estado.getCode();
    }

    default UUID mapClienteToId(Cliente cliente) {
        return cliente != null ? cliente.getId() : null;
    }
}

