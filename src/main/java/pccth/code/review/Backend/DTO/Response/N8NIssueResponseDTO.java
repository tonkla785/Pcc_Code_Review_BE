package pccth.code.review.Backend.DTO.Response;

import java.util.Date;

public class N8NIssueResponseDTO {
    private String scanId;
    private String projectKey;

    private String issueKey;
    private String ruleKey;
    private String type;        // BUG | VULNERABILITY | CODE_SMELL
    private String severity;    // BLOCKER | CRITICAL | MAJOR | MINOR
    private String component;   // file path
    private Integer line;
    private String message;     // short title
    private String status;      // OPEN | CONFIRMED | FIXED | IN_PROGRESS
    private Date createdAt;

    private String description;
    private String vulnerableCode;
    private String recommendedFix;

    public String getScanId() {
        return scanId;
    }

    public void setScanId(String scanId) {
        this.scanId = scanId;
    }

    public String getProjectKey() {
        return projectKey;
    }

    public void setProjectKey(String projectKey) {
        this.projectKey = projectKey;
    }

    public String getIssueKey() {
        return issueKey;
    }

    public void setIssueKey(String issueKey) {
        this.issueKey = issueKey;
    }

    public String getRuleKey() {
        return ruleKey;
    }

    public void setRuleKey(String ruleKey) {
        this.ruleKey = ruleKey;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public Integer getLine() {
        return line;
    }

    public void setLine(Integer line) {
        this.line = line;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
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

    // for test result from n8n
    @Override
    public String toString() {
        return """
                N8NIssueResponseDTO {
                  scanId          = %s
                  projectKey      = %s

                  issueKey        = %s
                  ruleKey         = %s
                  type            = %s
                  severity        = %s
                  component       = %s
                  line            = %s
                  message         = %s
                  status          = %s
                  createdAt       = %s

                  description     = %s
                  vulnerableCode  = %s
                  recommendedFix  = %s
                }
                """.formatted(
                scanId,
                projectKey,
                issueKey,
                ruleKey,
                type,
                severity,
                component,
                line,
                message,
                status,
                createdAt,
                description,
                vulnerableCode,
                recommendedFix
        );
    }
}
