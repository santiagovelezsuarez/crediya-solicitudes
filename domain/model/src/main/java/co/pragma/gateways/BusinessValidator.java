package co.pragma.gateways;

import reactor.core.publisher.Mono;

public interface BusinessValidator<I, O> {
    Mono<O> validate(I input);
}
