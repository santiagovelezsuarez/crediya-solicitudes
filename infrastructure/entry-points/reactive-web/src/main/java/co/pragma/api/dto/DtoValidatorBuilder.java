package co.pragma.api.dto;

import jakarta.validation.Validator;
import reactor.core.publisher.Mono;
import java.util.List;

public class DtoValidatorBuilder {

    private DtoValidatorBuilder() { }

    public static <T> Mono<T> validate(T dto, Validator validator) {
        return Mono.fromCallable(() -> validator.validate(dto))
                .flatMap(violations -> {
                    if (!violations.isEmpty()) {
                        List<DtoValidationException.FieldError> errors = violations.stream()
                                .map(attr -> new DtoValidationException.FieldError(
                                        attr.getPropertyPath().toString(),
                                        attr.getMessage()
                                ))
                                .toList();
                        return Mono.error(new DtoValidationException(errors));
                    }
                    return Mono.just(dto);
                });
    }
}
