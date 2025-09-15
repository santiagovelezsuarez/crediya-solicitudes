package co.pragma.r2dbc.mapper;

import co.pragma.model.estadosolicitud.EstadoSolicitudCodigo;
import co.pragma.model.solicitudprestamo.SolicitudPrestamo;
import co.pragma.r2dbc.entity.SolicitudPrestamoEntity;

public class SolicitudPrestamoEntityMapper {

    private  SolicitudPrestamoEntityMapper() {}

    public static SolicitudPrestamo toDomain(SolicitudPrestamoEntity entity) {
        if (entity == null) return null;

        return SolicitudPrestamo.builder()
                .id(entity.getId())
                .idCliente(entity.getIdCliente())
                .idTipoPrestamo(entity.getIdTipoPrestamo())
                .monto(entity.getMonto())
                .plazoEnMeses(entity.getPlazoEnMeses())
                .estado(entity.getIdEstado() != null
                        ? EstadoSolicitudCodigo.fromCode(entity.getIdEstado())
                        : null)
                .codigo(entity.getCodigo())
                .notificado(entity.getNotificado())
                .build();
    }

    public static SolicitudPrestamoEntity toEntity(SolicitudPrestamo model) {
        if (model == null) return null;

        return SolicitudPrestamoEntity.builder()
                .id(model.getId())
                .idCliente(model.getIdCliente())
                .idTipoPrestamo(model.getIdTipoPrestamo())
                .monto(model.getMonto())
                .plazoEnMeses(model.getPlazoEnMeses())
                .idEstado(model.getEstado() != null ? model.getEstado().getCode() : null)
                .codigo(model.getCodigo())
                .notificado(model.getNotificado())
                .build();
    }
}