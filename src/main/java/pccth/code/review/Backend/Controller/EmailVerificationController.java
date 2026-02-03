package pccth.code.review.Backend.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pccth.code.review.Backend.Service.EmailVerificationService;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/email-verification")
public class EmailVerificationController {

    private final EmailVerificationService service;

    public EmailVerificationController(EmailVerificationService service) {
        this.service = service;
    }

    @PostMapping("/send")
    public ResponseEntity<Void> send(@RequestBody Map<String, String> body) {
        UUID userId = UUID.fromString(body.get("userId"));
        service.sendVerificationEmail(userId);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/confirm")
    public ResponseEntity<Map<String, Object>> confirm(@RequestBody Map<String, String> body) {
        service.confirm(body.get("token"));
        return ResponseEntity.ok(Map.of("ok", true));
    }
}
