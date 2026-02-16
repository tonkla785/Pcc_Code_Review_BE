package pccth.code.review.Backend.Service;

import org.springframework.beans.factory.annotation.Autowired;
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
import pccth.code.review.Backend.Service.WebSocketNotificationService;


import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
public class EmailVerificationService {

    private final EmailVerificationTokenRepository tokenRepo;
    private final UserRepository userRepo;
    private final EmailService emailService;
    private final WebSocketNotificationService webSocketNotificationService;

    @Value("${app.backend.public-url:http://localhost:8080}")
    private String backendPublicUrl;

    @Value("${app.frontend.reset-url:http://localhost:4200/}")
    private String frontendBaseUrl;

    public EmailVerificationService(
            EmailVerificationTokenRepository tokenRepo,
            UserRepository userRepo,
            EmailService emailService,
            WebSocketNotificationService webSocketNotificationService
    ) {
        this.tokenRepo = tokenRepo;
        this.userRepo = userRepo;
        this.emailService = emailService;
        this.webSocketNotificationService = webSocketNotificationService;
    }



    @Transactional
    public void sendVerificationEmail(UUID userId) {
        UserEntity user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("USER_NOT_FOUND"));

        if ("VERIFIED".equalsIgnoreCase(user.getStatus())) return;

        if (!"PENDING_VERIFICATION".equalsIgnoreCase(user.getStatus())) {
            user.setStatus("PENDING_VERIFICATION");
            userRepo.save(user);
        }

        tokenRepo.invalidateAllActiveTokens(userId, Instant.now());

        String rawToken  = ResetTokenUtil.randomToken();
        String tokenHash = ResetTokenUtil.sha256Base64Url(rawToken);

        EmailVerificationTokenEntity t = new EmailVerificationTokenEntity();
        t.setUser(user);
        t.setTokenHash(tokenHash);
        t.setExpiresAt(Instant.now().plus(Duration.ofHours(24)));
        t.setUsedAt(null);

        tokenRepo.save(t);

        String link = backendPublicUrl + "/api/email-verification/confirm?token=" + rawToken;

        EmailRequestDTO dto = new EmailRequestDTO();
        dto.setType(EmailType.EmailVerification);
        dto.setEmail(user.getEmail());
        dto.setUsername(user.getUsername());
        dto.setLink(link);

        emailService.send(dto);



        webSocketNotificationService.sendUserVerifyStatus(user.getId(), user.getStatus());

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

        if (!"PENDING_VERIFICATION".equalsIgnoreCase(user.getStatus())) {
            throw new IllegalArgumentException("USER_NOT_PENDING_VERIFICATION");
        }

        user.setStatus("VERIFIED");
        userRepo.save(user);

        t.setUsedAt(Instant.now());
        tokenRepo.save(t);

        webSocketNotificationService.sendUserVerifyStatus(user.getId(), user.getStatus());

    }
}
