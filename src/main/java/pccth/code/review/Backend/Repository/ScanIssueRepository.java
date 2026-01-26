package pccth.code.review.Backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pccth.code.review.Backend.Entity.ScanIssueEntity;
import java.util.UUID;

public interface ScanIssueRepository extends JpaRepository<ScanIssueEntity, UUID> {
    boolean existsByScan_IdAndIssue_Id(UUID scanId, UUID issueId);
}
