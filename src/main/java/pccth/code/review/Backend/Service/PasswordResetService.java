package pccth.code.review.Backend.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pccth.code.review.Backend.DTO.Request.EmailRequestDTO;
import pccth.code.review.Backend.Entity.PasswordResetTokenEntity;
import pccth.code.review.Backend.Entity.UserEntity;
import pccth.code.review.Backend.EnumType.EmailType;
import pccth.code.review.Backend.Repository.PasswordResetTokenRepository;
import pccth.code.review.Backend.Repository.UserRepository;
import pccth.code.review.Backend.Util.ResetTokenUtil;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;

@Service
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.frontend.reset-url:http://localhost:4200/reset-password}")
    private String resetUrl;

    public PasswordResetService(
            UserRepository userRepository,
            PasswordResetTokenRepository tokenRepository,
            EmailService emailService,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void requestReset(String email) {
        var userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) return;

        UserEntity user = userOpt.get();

        String rawToken = ResetTokenUtil.randomToken();
        String tokenHash = ResetTokenUtil.sha256Base64Url(rawToken);

        PasswordResetTokenEntity token = new PasswordResetTokenEntity();
        token.setUserId(user.getId());
        token.setTokenHash(tokenHash);
        token.setExpiresAt(Instant.now().plus(Duration.ofMinutes(15)));
        tokenRepository.save(token);

        String link = resetUrl + "?token=" + URLEncoder.encode(rawToken, StandardCharsets.UTF_8);

        EmailRequestDTO dto = new EmailRequestDTO();
        dto.setType(EmailType.PasswordReset);
        dto.setEmail(user.getEmail());
        dto.setLink(link);
        dto.setUsername(user.getUsername());

        emailService.send(dto);
    }

    // 2) reset จริง
    @Transactional
    public void resetPassword(String rawToken, String newPassword) {
        String tokenHash = ResetTokenUtil.sha256Base64Url(rawToken);

        PasswordResetTokenEntity token = tokenRepository
                .findTopByTokenHashOrderByCreatedAtDesc(tokenHash)
                .orElseThrow(() -> new RuntimeException("INVALID_TOKEN"));

        if (token.isUsed()) throw new RuntimeException("TOKEN_USED");
        if (token.getExpiresAt().isBefore(Instant.now())) throw new RuntimeException("TOKEN_EXPIRED");

        UserEntity user = userRepository.findById(token.getUserId())
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        token.setUsedAt(Instant.now());
        tokenRepository.save(token);
    }
}
