package co.pragma.r2dbc.mapper;

import co.pragma.model.estadosolicitud.EstadoSolicitudCodigo;
import co.pragma.model.solicitudprestamo.SolicitudPrestamo;
import co.pragma.r2dbc.entity.SolicitudPrestamoEntity;
import org.springframework.stereotype.Component;

@Component
public class SolicitudPrestamoEntityMapper {

    private  SolicitudPrestamoEntityMapper() {}

    public SolicitudPrestamo toDomain(SolicitudPrestamoEntity entity) {
        return SolicitudPrestamo.builder()
                .id(entity.getId())
                .codigo(entity.getCodigo())
                .idCliente(entity.getIdCliente())
                .idTipoPrestamo(entity.getIdTipoPrestamo())
                .monto(entity.getMonto())
                .plazoEnMeses(entity.getPlazoEnMeses())
                .tasaInteres(entity.getTasaInteres())
                .estado(entity.getIdEstado() != null
                        ? EstadoSolicitudCodigo.fromCode(entity.getIdEstado())
                        : null)
                .notificado(entity.getNotificado())
                .tasaInteres(entity.getTasaInteres())
                .build();
    }

    public SolicitudPrestamoEntity toEntity(SolicitudPrestamo model) {
        return SolicitudPrestamoEntity.builder()
                .id(model.getId())
                .codigo(model.getCodigo())
                .idCliente(model.getIdCliente())
                .idEstado(model.getEstado() != null ? model.getEstado().getCode() : null)
                .monto(model.getMonto())
                .plazoEnMeses(model.getPlazoEnMeses())
                .tasaInteres(model.getTasaInteres())
                .idTipoPrestamo(model.getIdTipoPrestamo())
                .notificado(model.getNotificado())
                .tasaInteres(model.getTasaInteres())
                .build();
    }
}