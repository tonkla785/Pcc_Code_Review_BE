package pccth.code.review.Backend.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pccth.code.review.Backend.DTO.Request.EmailRequestDTO;
import pccth.code.review.Backend.Entity.EmailVerificationTokenEntity;
import pccth.code.review.Backend.Entity.UserEntity;
import pccth.code.review.Backend.EnumType.EmailType;
import pccth.code.review.Backend.Repository.EmailVerificationTokenRepository;
import pccth.code.review.Backend.Repository.UserRepository;
import pccth.code.review.Backend.Util.ResetTokenUtil;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
public class EmailVerificationService {

    private final EmailVerificationTokenRepository tokenRepo;
    private final UserRepository userRepo;
    private final EmailService emailService;

    @Value("${app.frontend.reset-url:http://localhost:4200/}")
    private String frontendBaseUrl;

    public EmailVerificationService(
            EmailVerificationTokenRepository tokenRepo,
            UserRepository userRepo,
            EmailService emailService
    ) {
        this.tokenRepo = tokenRepo;
        this.userRepo = userRepo;
        this.emailService = emailService;
    }

    @Transactional
    public void sendVerificationEmail(UUID userId) {
        UserEntity user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("USER_NOT_FOUND"));

        // ✅ verified แล้วไม่ต้องส่ง
        if ("ACTIVE".equalsIgnoreCase(user.getStatus())) return;

        // ✅ invalidate token เก่าที่ active (กัน spam/ซ้อน)
        tokenRepo.invalidateAllActiveTokens(userId, Instant.now());

        // ✅ ใช้ util ของมึง
        String rawToken  = ResetTokenUtil.randomToken();
        String tokenHash = ResetTokenUtil.sha256Base64Url(rawToken);

        EmailVerificationTokenEntity t = new EmailVerificationTokenEntity();
        t.setUser(user);
        t.setTokenHash(tokenHash);
        t.setExpiresAt(Instant.now().plus(Duration.ofHours(24)));
        t.setUsedAt(null);

        tokenRepo.save(t);

        String link = frontendBaseUrl + "/verify-email?token=" + rawToken;

        EmailRequestDTO dto = new EmailRequestDTO();
        dto.setType(EmailType.EmailVerification);
        dto.setEmail(user.getEmail());
        dto.setUsername(user.getUsername());
        dto.setLink(link);

        emailService.send(dto);
    }

    @Transactional
    public void confirm(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            throw new IllegalArgumentException("TOKEN_REQUIRED");
        }

        String tokenHash = ResetTokenUtil.sha256Base64Url(rawToken);

        EmailVerificationTokenEntity t = tokenRepo.findByTokenHash(tokenHash)
                .orElseThrow(() -> new IllegalArgumentException("INVALID_TOKEN"));

        if (t.isUsed()) throw new IllegalArgumentException("TOKEN_ALREADY_USED");
        if (t.isExpired()) throw new IllegalArgumentException("TOKEN_EXPIRED");

        UserEntity user = t.getUser();

        // ✅ mark verified
        user.setStatus("ACTIVE"); // หรือ "PENDING_VERIFICATION" ตอนสมัคร
        userRepo.save(user);

        // ✅ mark token used
        t.setUsedAt(Instant.now());
        tokenRepo.save(t);
    }
}
