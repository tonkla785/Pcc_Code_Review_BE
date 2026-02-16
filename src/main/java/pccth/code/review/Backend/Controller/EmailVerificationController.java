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
        try {
            service.confirm(token);

            // redirect ไปหน้า FE สำเร็จ (หรือจะตอบเป็น plain text ก็ได้)
            URI ok = URI.create(frontendBaseUrl + "/verify-success");
            return ResponseEntity.status(302).location(ok).build();

        } catch (Exception ex) {
            URI fail = URI.create(frontendBaseUrl + "/verify-failed");
            return ResponseEntity.status(302).location(fail).build();
        }
    }
}

