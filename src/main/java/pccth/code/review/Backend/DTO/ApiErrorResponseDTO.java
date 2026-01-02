package pccth.code.review.Backend.DTO;

import java.util.Map;
public class ApiErrorResponseDTO {
    private final int status;
    private final String message;
    private final Map<String, String> errors;

    public ApiErrorResponseDTO(int status, String message) {
        this(status, message, null);
    }

    public ApiErrorResponseDTO(int status, String message, Map<String, String> errors) {
        this.status = status;
        this.message = message;
        this.errors = errors;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
