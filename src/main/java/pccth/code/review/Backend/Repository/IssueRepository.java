package pccth.code.review.Backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pccth.code.review.Backend.DTO.Response.IssueStatusCountDTO;
import pccth.code.review.Backend.Entity.IssueEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IssueRepository extends JpaRepository<IssueEntity, UUID> {

    Optional<IssueEntity> findByIssueKey(String issueKey);

    List<IssueEntity> findAllByScan_Id(UUID scanId);

    Optional<IssueEntity> findByScan_IdAndIssueKey(UUID scanId, String issueKey);

    @Query("select i.issueKey from IssueEntity i where i.scan.id = :scanId")
    List<String> findIssueKeysByScanId(UUID scanId);
}
