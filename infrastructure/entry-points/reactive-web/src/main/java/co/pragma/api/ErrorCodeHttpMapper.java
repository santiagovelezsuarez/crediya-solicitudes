package co.pragma.api;

import co.pragma.error.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ErrorCodeHttpMapper {

    private static final Map<ErrorCode, HttpStatus> MAPPINGS = Map.of(
            ErrorCode.CLIENTE_NOT_FOUND, HttpStatus.NOT_FOUND,
            ErrorCode.TIPO_PRESTAMO_NOT_FOUND, HttpStatus.NOT_FOUND
    );

    public HttpStatus toHttpStatus(ErrorCode code) {
        return MAPPINGS.getOrDefault(code, HttpStatus.BAD_REQUEST);
    }
}
