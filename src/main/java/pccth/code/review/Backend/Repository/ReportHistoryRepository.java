package pccth.code.review.Backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pccth.code.review.Backend.Entity.ReportHistoryEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReportHistoryRepository extends JpaRepository<ReportHistoryEntity, UUID> {

    List<ReportHistoryEntity> findByUserIdOrderByGeneratedAtDesc(UUID userId);

    // Search by project name keyword (case-insensitive)
    @Query("SELECT r FROM ReportHistoryEntity r WHERE r.user.id = :userId AND LOWER(r.projectName) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY r.generatedAt DESC")
    List<ReportHistoryEntity> searchByProjectName(@Param("userId") UUID userId, @Param("keyword") String keyword);
}
