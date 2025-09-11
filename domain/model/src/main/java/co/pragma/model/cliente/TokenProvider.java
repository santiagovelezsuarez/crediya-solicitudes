package co.pragma.model.cliente;

import reactor.core.publisher.Mono;

public interface TokenProvider {
    Mono<String> getCurrentToken();
}
