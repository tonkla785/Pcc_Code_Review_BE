package pccth.code.review.Backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pccth.code.review.Backend.Entity.IssueEntity;

import java.util.UUID;

@Repository
public interface IssueRepository extends JpaRepository<IssueEntity, UUID> {
}
