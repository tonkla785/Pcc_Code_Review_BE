package pccth.code.review.Backend.Controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pccth.code.review.Backend.Service.PasswordResetService;
import pccth.code.review.Backend.Util.RequestOriginUtil;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    @Value("${cors.allowed-origins}")
    private List<String> allowedOrigins;

    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgot(@RequestBody Map<String, String> body, HttpServletRequest request) {
        // ดึง Frontend Origin แบบ Dynamic จาก request
        String frontendBaseUrl = RequestOriginUtil.extractFrontendOrigin(request, allowedOrigins);
        passwordResetService.requestReset(body.get("email"), frontendBaseUrl);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> reset(@RequestBody Map<String, String> body) {
        passwordResetService.resetPassword(body.get("token"), body.get("newPassword"));
        return ResponseEntity.ok().build();
    }
}
