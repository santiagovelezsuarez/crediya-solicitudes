package co.pragma.usecase.solicitud.businessrules;

import co.pragma.exception.ClienteNotFoundException;
import co.pragma.gateways.BusinessValidator;
import co.pragma.model.cliente.Cliente;
import co.pragma.model.cliente.DocumentoIdentidadVO;
import co.pragma.model.cliente.gateways.UsuariosPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ClienteValidator implements BusinessValidator<DocumentoIdentidadVO, Cliente> {

    private final UsuariosPort usuariosPort;

    @Override
    public Mono<Cliente> validate(DocumentoIdentidadVO documentoIdentidad) {
        return usuariosPort.findClienteByDocumento(documentoIdentidad)
                .switchIfEmpty(Mono.error(new ClienteNotFoundException("El cliente no está registrado")));
    }
}
