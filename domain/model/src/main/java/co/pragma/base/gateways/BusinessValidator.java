package co.pragma.base.gateways;

import reactor.core.publisher.Mono;

public interface BusinessValidator<T> {
    Mono<T> validate(T model);
}

