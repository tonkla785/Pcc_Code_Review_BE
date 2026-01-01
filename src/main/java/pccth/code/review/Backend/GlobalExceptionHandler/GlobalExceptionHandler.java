package pccth.code.review.Backend.GlobalExceptionHandler;

import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Validation จาก @RequestBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult()
                .getFieldErrors()
                .forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));

        return ResponseEntity.badRequest().body(Map.of(
                "responseStatus", 400,
                "responseMessage", "Validation failed",
                "errors", errors
        ));
    }

    // Validation จาก @PathVariable / @RequestParam
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraint(ConstraintViolationException e) {
        Map<String, String> errors = new HashMap<>();
        e.getConstraintViolations().forEach(v ->
                errors.put(v.getPropertyPath().toString(), v.getMessage())
        );

        return ResponseEntity.badRequest().body(Map.of(
                "responseStatus", 400,
                "responseMessage", "Validation failed",
                "errors", errors
        ));
    }

    // IllegalArgumentException → 400 Bad Request
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArg(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of(
                "responseStatus", 400,
                "responseMessage", e.getMessage()
        ));
    }

    // RuntimeException อื่น ๆ เช่น refresh token / JWT expired → 401 Unauthorized
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException e) {
        String msg = e.getMessage() != null ? e.getMessage() : "Internal error";

        if (msg.contains("Refresh token missing") ||
                msg.contains("Invalid refresh token") ||
                msg.contains("JWT token expired")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "responseStatus", 401,
                    "responseMessage", msg
            ));
        }

        // RuntimeException อื่น ๆ → 400
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "responseStatus", 400,
                "responseMessage", msg
        ));
    }

    // ไม่เจอ resource → 404 Not Found
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> handleNotFound(NoSuchElementException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "responseStatus", 404,
                "responseMessage", e.getMessage()
        ));
    }

    // Data integrity เช่น duplicate key → 400
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrity(DataIntegrityViolationException e) {
        return ResponseEntity.badRequest().body(Map.of(
                "responseStatus", 400,
                "responseMessage", "Email, username or phone already exists"
        ));
    }

    // ResponseStatusException → ใช้ status ตาม exception
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> handleResponseStatus(ResponseStatusException e) {
        return ResponseEntity.status(e.getStatusCode()).body(Map.of(
                "responseStatus", e.getStatusCode().value(),
                "responseMessage", e.getReason()
        ));
    }

    // Fallback สำหรับ exception อื่น ๆ → 500 Internal Server Error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneral(Exception e) {
        e.printStackTrace(); // สำหรับ debug
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "responseStatus", 500,
                "responseMessage", e.getMessage()
        ));
    }
}
