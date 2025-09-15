package co.pragma.api.dto;

import co.pragma.api.dto.response.ErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DtoValidator {

    private final Validator validator;

    public <T> Mono<T> validate(T dto) {
        Set<ConstraintViolation<T>> fieldErrors = validator.validate(dto);

        if (!fieldErrors.isEmpty()) {
            List<ErrorResponse.FieldError> errors = fieldErrors.stream()
                    .map(attr -> new ErrorResponse.FieldError(
                            attr.getPropertyPath().toString(),
                            attr.getMessage()
                    ))
                    .toList();

            return Mono.error(new DtoValidationException(errors));
        }

        return Mono.just(dto);
    }
}
