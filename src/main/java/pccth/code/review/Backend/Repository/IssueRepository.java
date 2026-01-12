package pccth.code.review.Backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pccth.code.review.Backend.DTO.Response.IssueStatusCountDTO;
import pccth.code.review.Backend.Entity.IssueEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface IssueRepository extends JpaRepository<IssueEntity, UUID> {

    @Query("""
        select new pccth.code.review.Backend.DTO.Response.IssueStatusCountDTO(
            i.severity, count(i)
        )
        from IssueEntity i
        join i.scan s
        where s.project.id = :projectId
        group by i.severity
    """)
    List<IssueStatusCountDTO> countIssuesBySeverityInProject(@Param("projectId") UUID projectId);
}
