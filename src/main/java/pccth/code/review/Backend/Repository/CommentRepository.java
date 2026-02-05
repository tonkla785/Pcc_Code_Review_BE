package pccth.code.review.Backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pccth.code.review.Backend.Entity.CommentEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, UUID> {

    /**
     * Find all distinct user IDs who have commented on a specific issue
     */
    @Query("SELECT DISTINCT c.user.id FROM CommentEntity c WHERE c.issue.id = :issueId")
    List<UUID> findDistinctUserIdsByIssueId(@Param("issueId") UUID issueId);
}
