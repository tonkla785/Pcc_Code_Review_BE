package pccth.code.review.Backend.Controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pccth.code.review.Backend.Service.EmailVerificationService;
import pccth.code.review.Backend.Util.RequestOriginUtil;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/email-verification")
public class EmailVerificationController {

    private final EmailVerificationService service;

    @Value("${cors.allowed-origins}")
    private List<String> allowedOrigins;

    public EmailVerificationController(EmailVerificationService service) {
        this.service = service;
    }

    @PostMapping("/send")
    public ResponseEntity<Void> send(@RequestBody Map<String, String> body, HttpServletRequest request) {
        UUID userId = UUID.fromString(body.get("userId"));

        // ดึง URL แบบ Dynamic จาก request ที่ FE ยิงเข้ามา
        String frontendOrigin = RequestOriginUtil.extractFrontendOrigin(request, allowedOrigins);
        String backendBaseUrl = RequestOriginUtil.extractBackendBaseUrl(request);

        service.sendVerificationEmail(userId, backendBaseUrl, frontendOrigin);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/confirm")
    public ResponseEntity<Void> confirmByBrowser(
            @RequestParam("token") String token,
            @RequestParam(value = "origin", required = false) String origin) {

        // validate origin ที่ฝังมาใน link กับ CORS whitelist เพื่อป้องกัน Open Redirect
        String frontendBaseUrl = RequestOriginUtil.validateOrigin(origin, allowedOrigins);

        try {
            service.confirm(token);

            // redirect ไปหน้า FE สำเร็จ
            URI ok = URI.create(frontendBaseUrl + "/verify-success");
            return ResponseEntity.status(302).location(ok).build();

        } catch (Exception ex) {
            URI fail = URI.create(frontendBaseUrl + "/verify-failed");
            return ResponseEntity.status(302).location(fail).build();
        }
    }
}
