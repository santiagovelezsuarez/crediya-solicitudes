package co.pragma.usecase.solicitud;

import co.pragma.exception.ClienteNotFoundException;
import co.pragma.exception.TipoPrestamoNotFoundException;
import co.pragma.model.cliente.Cliente;
import co.pragma.model.cliente.DocumentoIdentidadVO;
import co.pragma.model.solicitudprestamo.SolicitudPrestamo;
import co.pragma.model.solicitudprestamo.gateways.SolicitudPrestamoRepository;
import co.pragma.model.tipoprestamo.TipoPrestamo;
import co.pragma.model.tipoprestamo.TipoPrestamoVO;
import co.pragma.usecase.solicitud.businessrules.ClienteValidator;
import co.pragma.usecase.solicitud.businessrules.TipoPrestamoValidator;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class SolicitudUseCase {

    private static final int DEFAULT_ESTADO_ID = 1;
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

            solicitud.setIdCliente(cliente.getId());
            solicitud.setIdTipoPrestamo(tipoPrestamo.getId());
            solicitud.setIdEstado(DEFAULT_ESTADO_ID);

            return solicitud;
        }).flatMap(solicitudPrestamoRepository::save);
    }
}
