package co.pragma.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private Instant timestamp;
    private int status;
    private String error;
    private String message;
    private String path;

    @JsonProperty("validation_errors")
    private List<FieldError> validationErrors;

    @Data
    @AllArgsConstructor
    public static class FieldError {
        private String field;
        private String message;
    }
}
