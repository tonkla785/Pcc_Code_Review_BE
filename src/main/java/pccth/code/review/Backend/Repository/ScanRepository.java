package pccth.code.review.Backend.Repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pccth.code.review.Backend.DTO.Response.SeverityCountDTO;
import pccth.code.review.Backend.Entity.ScanEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface ScanRepository extends JpaRepository<ScanEntity, UUID> {
    List<ScanEntity> findByProjectId(UUID projectId);

    @Query("""
                select distinct s
                from ScanEntity s
                left join fetch s.scanIssues si
                left join fetch si.issue i
                where s.project.id = :projectId
            """)
    List<ScanEntity> findScansWithIssuesByProjectId(@Param("projectId") UUID projectId);

    // เรียงลำดับตาม startedAt จากใหม่ไปเก่า
    List<ScanEntity> findAllByOrderByStartedAtDesc();
}
