package pccth.code.review.Backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pccth.code.review.Backend.Entity.IssueEntity;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IssueRepository extends JpaRepository<IssueEntity, UUID> {
    Optional<IssueEntity> findById(UUID id);
    List<IssueEntity> findByTypeIn(Collection<String> types);

    @Query("""
                select i
                from IssueEntity i
                left join fetch i.detail
                where i.project.id = :projectId
            """)
    List<IssueEntity> findByProjectIdWithDetail(@Param("projectId") UUID projectId);
}
