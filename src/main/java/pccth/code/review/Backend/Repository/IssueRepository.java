package pccth.code.review.Backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pccth.code.review.Backend.DTO.Response.IssueStatusCountDTO;
import pccth.code.review.Backend.Entity.IssueEntity;
import pccth.code.review.Backend.Entity.ScanIssueEntity;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IssueRepository extends JpaRepository<IssueEntity, UUID> {
    Optional<IssueEntity> findByIssueKey(String issueKey);

    @Query(value = """
            INSERT INTO issues (
                issue_key, type, severity, rule_key, component, line, message, status, created_at
            ) VALUES (
                :issueKey, :type, :severity, :ruleKey, :component, :line, :message, :status, :createdAt
            )
            ON CONFLICT (issue_key)
            DO UPDATE SET
                type = EXCLUDED.type,
                severity = EXCLUDED.severity,
                rule_key = EXCLUDED.rule_key,
                component = EXCLUDED.component,
                line = EXCLUDED.line,
                message = EXCLUDED.message,
                status = EXCLUDED.status
            RETURNING id
            """, nativeQuery = true)
    UUID upsertIssue(
            @Param("issueKey") String issueKey,
            @Param("type") String type,
            @Param("severity") String severity,
            @Param("ruleKey") String ruleKey,
            @Param("component") String component,
            @Param("line") Integer line,
            @Param("message") String message,
            @Param("status") String status,
            @Param("createdAt") Instant createdAt
    );
}
