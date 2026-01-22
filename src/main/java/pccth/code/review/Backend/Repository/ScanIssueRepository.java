package pccth.code.review.Backend.Repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pccth.code.review.Backend.Entity.IssueEntity;
import pccth.code.review.Backend.Entity.ScanIssueEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ScanIssueRepository extends JpaRepository<ScanIssueEntity, UUID> {
    @Query("""
    select i.severity, count(i)
    from ScanIssueEntity si
    join si.scan s
    join si.issue i
    where s.project.id = :projectId
    group by i.severity
""")
    List<Object[]> countSeverityByProject(@Param("projectId") UUID projectId);

    List<ScanIssueEntity> findAllByScan_Id(UUID scanId);

    boolean existsByScan_IdAndIssue_Id(UUID scanId, UUID issueId);
}
