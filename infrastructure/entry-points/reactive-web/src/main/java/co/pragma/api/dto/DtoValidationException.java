package co.pragma.api.dto;

import lombok.Getter;
import java.util.List;

@Getter
public class DtoValidationException extends RuntimeException {

    private final transient List<FieldError> errors;

    public DtoValidationException(List<FieldError> errors) {
        super("Error, verifique la informacion enviada");
        this.errors = errors;
    }

    public record FieldError(String field, String message) {
    }
}
