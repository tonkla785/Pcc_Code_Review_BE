package pccth.code.review.Backend.Entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "issue_details")
public class IssueDetailEntity {

    @Id
    @Column(name = "issue_id")
    private UUID issueId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "issue_id")
    private IssueEntity issue;

    @Column(name = "description")
    private String description;

    @Column(name = "vulnerable_code")
    private String vulnerableCode;

    @Column(name = "recommended_fix")
    private String recommendedFix;

    @Column(name = "recommended_fix_by_ai")
    private String recommendedFixByAi;

    @Column(name = "status")
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRecommendedFixByAi() {
        return recommendedFixByAi;
    }

    public void setRecommendedFixByAi(String recommendedFixByAi) {
        this.recommendedFixByAi = recommendedFixByAi;
    }

    public IssueEntity getIssue() {
        return issue;
    }

    public void setIssue(IssueEntity issue) {
        this.issue = issue;
    }

    public UUID getIssueId() {
        return issueId;
    }

    public void setIssueId(UUID issueId) {
        this.issueId = issueId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVulnerableCode() {
        return vulnerableCode;
    }

    public void setVulnerableCode(String vulnerableCode) {
        this.vulnerableCode = vulnerableCode;
    }

    public String getRecommendedFix() {
        return recommendedFix;
    }

    public void setRecommendedFix(String recommendedFix) {
        this.recommendedFix = recommendedFix;
    }
}
