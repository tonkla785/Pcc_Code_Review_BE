package pccth.code.review.Backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pccth.code.review.Backend.Entity.PasswordResetTokenEntity;

import java.util.Optional;
import java.util.UUID;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetTokenEntity, UUID> {
    Optional<PasswordResetTokenEntity> findTopByTokenHashOrderByCreatedAtDesc(String tokenHash);
}
