package pccth.code.review.Backend.GlobalExceptionHandler;

import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import pccth.code.review.Backend.DTO.ApiErrorResponseDTO;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Validation จาก @RequestBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleValidation(
            MethodArgumentNotValidException e) {

        Map<String, String> errors = new HashMap<>();
        e.getBindingResult()
                .getFieldErrors()
                .forEach(err ->
                        errors.put(err.getField(), err.getDefaultMessage())
                );

        ApiErrorResponseDTO response =
                new ApiErrorResponseDTO(
                        HttpStatus.BAD_REQUEST.value(),
                        "Validation failed",
                        errors
                );

        return ResponseEntity.badRequest().body(response);
    }

    // Validation จาก @PathVariable / @RequestParam
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleConstraint(
            ConstraintViolationException e) {

        Map<String, String> errors = new HashMap<>();
        e.getConstraintViolations().forEach(v ->
                errors.put(v.getPropertyPath().toString(), v.getMessage())
        );

        ApiErrorResponseDTO response =
                new ApiErrorResponseDTO(
                        HttpStatus.BAD_REQUEST.value(),
                        "Validation failed",
                        errors
                );

        return ResponseEntity.badRequest().body(response);
    }

    // IllegalArgumentException 400
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleIllegalArg(
            IllegalArgumentException e) {

        ApiErrorResponseDTO response =
                new ApiErrorResponseDTO(
                        HttpStatus.BAD_REQUEST.value(),
                        e.getMessage()
                );

        return ResponseEntity.badRequest().body(response);
    }

    // RuntimeException (JWT / Refresh Token)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleRuntimeException(
            RuntimeException e) {

        String message = e.getMessage() != null
                ? e.getMessage()
                : "Internal error";

        HttpStatus status = HttpStatus.BAD_REQUEST;

        if (message.contains("Refresh token missing")
                || message.contains("Invalid refresh token")
                || message.contains("JWT token expired")) {
            status = HttpStatus.UNAUTHORIZED;
        }

        ApiErrorResponseDTO response =
                new ApiErrorResponseDTO(
                        status.value(),
                        message
                );

        return ResponseEntity.status(status).body(response);
    }

    // Not Found 404
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleNotFound(
            NoSuchElementException e) {

        ApiErrorResponseDTO response =
                new ApiErrorResponseDTO(
                        HttpStatus.NOT_FOUND.value(),
                        e.getMessage()
                );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // Duplicate key / Data integrity
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleDataIntegrity(
            DataIntegrityViolationException e) {

        ApiErrorResponseDTO response =
                new ApiErrorResponseDTO(
                        HttpStatus.BAD_REQUEST.value(),
                        "Email, username or phone already exists"
                );

        return ResponseEntity.badRequest().body(response);
    }

    // ResponseStatusException
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleResponseStatus(
            ResponseStatusException e) {

        ApiErrorResponseDTO response =
                new ApiErrorResponseDTO(
                        e.getStatusCode().value(),
                        e.getReason()
                );

        return ResponseEntity.status(e.getStatusCode()).body(response);
    }

    // Fallback 500 exception อื่นๆ
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponseDTO> handleGeneral(Exception e) {
        e.printStackTrace(); //เอาไว้ debug
        ApiErrorResponseDTO response =
                new ApiErrorResponseDTO(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Internal server error"
                );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
}
