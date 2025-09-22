package co.pragma.api.adapter;

import co.pragma.exception.ErrorCode;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorCodeHttpMapperTest {

    private final ErrorCodeHttpMapper mapper = new ErrorCodeHttpMapper();

    @Test
    void shouldMapForbiddenErrorCodeToHttpStatusForbidden() {
        HttpStatus status = mapper.toHttpStatus(ErrorCode.FORBIDDEN);
        assertThat(status).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void shouldReturnBadRequestForUnmappedErrorCode() {
        HttpStatus status = mapper.toHttpStatus(ErrorCode.DB_ERROR); // cualquier error no mapeado
        assertThat(status).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
