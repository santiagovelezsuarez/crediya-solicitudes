package co.pragma.usecase.solicitud;

import co.pragma.model.cliente.DocumentoIdentidadVO;
import co.pragma.model.estadosolicitud.EstadoSolicitud;
import co.pragma.model.estadosolicitud.EstadoSolicitudCodigo;
import co.pragma.model.solicitudprestamo.SolicitudPrestamo;
import co.pragma.model.solicitudprestamo.gateways.SolicitudPrestamoRepository;
import co.pragma.model.tipoprestamo.TipoPrestamoVO;
import co.pragma.usecase.solicitud.businessrules.ClienteValidator;
import co.pragma.usecase.solicitud.businessrules.TipoPrestamoValidator;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class SolicitudPrestamoUseCase {

    private final SolicitudPrestamoRepository solicitudPrestamoRepository;
    private final TipoPrestamoValidator tipoPrestamoValidator;
    private final ClienteValidator clienteValidator;

    public Mono<SolicitudPrestamo> createSolicitud(
            SolicitudPrestamo solicitud,
            DocumentoIdentidadVO documento,
            TipoPrestamoVO tipoPrestamoVO) {

        return Mono.zip(
                clienteValidator.validate(documento),
                tipoPrestamoValidator.validate(tipoPrestamoVO)
        ).map(tuple -> {
            var cliente = tuple.getT1();
            var tipoPrestamo = tuple.getT2();
            var estadoInicial = EstadoSolicitudCodigo.PENDIENTE_REVISION;

            return solicitud.toBuilder()
                    .idCliente(cliente.getId())
                    .idTipoPrestamo(tipoPrestamo.getId())
                    .estado(estadoInicial)
                    .build();
        }).flatMap(solicitudPrestamoRepository::save);
    }
}
