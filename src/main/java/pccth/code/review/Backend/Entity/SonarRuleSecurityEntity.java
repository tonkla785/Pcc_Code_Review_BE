package pccth.code.review.Backend.Entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "sonar_rule_security")
public class SonarRuleSecurityEntity {

    @Id
    @Column(name = "rule_key", length = 150)
    private String ruleKey;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "owasp_categories", columnDefinition = "jsonb")
    private List<String> owaspCategories = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "security_categories", columnDefinition = "jsonb")
    private List<String> securityCategories = new ArrayList<>();

    @Column(name = "updated_at")
    private Date updatedAt;

    public String getRuleKey() {
        return ruleKey;
    }

    public void setRuleKey(String ruleKey) {
        this.ruleKey = ruleKey;
    }

    public List<String> getOwaspCategories() {
        return owaspCategories;
    }

    public void setOwaspCategories(List<String> owaspCategories) {
        this.owaspCategories = owaspCategories;
    }

    public List<String> getSecurityCategories() {
        return securityCategories;
    }

    public void setSecurityCategories(List<String> securityCategories) {
        this.securityCategories = securityCategories;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
