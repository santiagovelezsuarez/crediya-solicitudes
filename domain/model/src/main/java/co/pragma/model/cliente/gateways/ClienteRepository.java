package co.pragma.model.cliente.gateways;

import co.pragma.model.cliente.Cliente;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.UUID;

public interface ClienteRepository {
    Mono<Cliente> findById(UUID id);
    Flux<Cliente> findByIdIn(List<UUID> clienteIds);
}