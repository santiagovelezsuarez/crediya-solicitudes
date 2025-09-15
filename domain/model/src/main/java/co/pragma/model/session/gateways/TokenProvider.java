package co.pragma.model.session.gateways;

import reactor.core.publisher.Mono;

public interface TokenProvider {
    Mono<String> getCurrentToken();
}
