package co.pragma.model.cliente.gateways;

import co.pragma.model.cliente.ClienteInfo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface UsuarioPort {
    Flux<ClienteInfo> getClientesByIdIn(List<UUID> userIds);
    Mono<ClienteInfo> getClienteById(UUID id);
}
