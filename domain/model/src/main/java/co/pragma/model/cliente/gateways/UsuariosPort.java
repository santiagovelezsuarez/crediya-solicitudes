package co.pragma.model.cliente.gateways;

import co.pragma.model.cliente.Cliente;
import co.pragma.model.cliente.DocumentoIdentidadVO;
import reactor.core.publisher.Mono;

public interface UsuariosPort {
    Mono<Cliente> findClienteByDocumento(DocumentoIdentidadVO documentoIdentidad);
}
