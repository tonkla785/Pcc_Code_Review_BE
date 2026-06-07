package pccth.code.review.Backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pccth.code.review.Backend.Entity.SonarRuleSecurityEntity;

public interface SonarRuleSecurityRepository extends JpaRepository<SonarRuleSecurityEntity, String> {

}
