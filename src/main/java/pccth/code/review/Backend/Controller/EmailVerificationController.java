package pccth.code.review.Backend.Controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pccth.code.review.Backend.Service.EmailVerificationService;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/email-verification")
public class EmailVerificationController {

    private final EmailVerificationService service;


    @Value("${app.frontend.base-url:http://localhost:4200}")
    private String frontendBaseUrl;

    public EmailVerificationController(EmailVerificationService service) {
        this.service = service;
    }

    @PostMapping("/send")
    public ResponseEntity<Void> send(@RequestBody Map<String, String> body) {
        UUID userId = UUID.fromString(body.get("userId"));
        service.sendVerificationEmail(userId);
        return ResponseEntity.accepted().build();
    }

//    @PostMapping("/confirm")
//    public ResponseEntity<Map<String, Object>> confirm(@RequestBody Map<String, String> body) {
//        service.confirm(body.get("token"));
//        return ResponseEntity.ok(Map.of("ok", true));
//    }

    @GetMapping("/confirm")
    public ResponseEntity<Void> confirmByBrowser(@RequestParam("token") String token) {
        String status;
        try {
            status = service.confirm(token);
        } catch (Exception ex) {
            status = "INVALID";
        }

        URI target = switch (status) {
            case "VERIFIED" -> URI.create(frontendBaseUrl + "/verify-success");
            case "ALREADY_VERIFIED" -> URI.create(frontendBaseUrl + "/verify-success?status=already");
            case "EXPIRED" -> URI.create(frontendBaseUrl + "/verify-failed?reason=expired");
            default -> URI.create(frontendBaseUrl + "/verify-failed?reason=invalid");
        };

        return ResponseEntity.status(302).location(target).build();
    }
}

