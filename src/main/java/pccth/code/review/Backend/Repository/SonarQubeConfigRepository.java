package pccth.code.review.Backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pccth.code.review.Backend.Entity.SonarQubeConfigEntity;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SonarQubeConfigRepository extends JpaRepository<SonarQubeConfigEntity, UUID> {

    Optional<SonarQubeConfigEntity> findByUserId(UUID userId);
}
