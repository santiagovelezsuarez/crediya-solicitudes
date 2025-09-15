package co.pragma.api.dto;

import co.pragma.api.dto.response.ErrorResponse;
import lombok.Getter;
import java.util.List;

@Getter
public class DtoValidationException extends RuntimeException {

    private final transient List<ErrorResponse.FieldError> errors;

    public DtoValidationException(List<ErrorResponse.FieldError> errors) {
        super("Error, verifique la información enviada");
        this.errors = errors;
    }
}
