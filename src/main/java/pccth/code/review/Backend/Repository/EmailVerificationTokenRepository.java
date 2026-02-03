package pccth.code.review.Backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import pccth.code.review.Backend.Entity.EmailVerificationTokenEntity;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface EmailVerificationTokenRepository
        extends JpaRepository<EmailVerificationTokenEntity, UUID> {

    Optional<EmailVerificationTokenEntity> findByTokenHash(String tokenHash);

    @Modifying
    @Query("""
        update EmailVerificationTokenEntity t
           set t.usedAt = :now
         where t.user.id = :userId
           and t.usedAt is null
    """)
    int invalidateAllActiveTokens(UUID userId, Instant now);
}
