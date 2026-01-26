package pccth.code.review.Backend.DTO.Response;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class IssuesResponseDTO {
    private UUID id;
    private UUID scanId;
    private UUID projcetId;
    private String issueKey;
    private String type;
    private String severity;
    private String ruleKey;
    private String component;
    private Integer line;
    private String message;
    private UUID assignedTo;
    private String status;
    private Date createdAt;
    private List<CommentResponseDTO> commentData = new ArrayList<>();

    public UUID getProjcetId() {
        return projcetId;
    }

    public void setProjcetId(UUID projcetId) {
        this.projcetId = projcetId;
    }

    public String getRuleKey() {
        return ruleKey;
    }

    public void setRuleKey(String ruleKey) {
        this.ruleKey = ruleKey;
    }

    public Integer getLine() {
        return line;
    }

    public void setLine(Integer line) {
        this.line = line;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getScanId() {
        return scanId;
    }

    public void setScanId(UUID scanId) {
        this.scanId = scanId;
    }

    public String getIssueKey() {
        return issueKey;
    }

    public void setIssueKey(String issueKey) {
        this.issueKey = issueKey;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UUID getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(UUID assignedTo) {
        this.assignedTo = assignedTo;
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

    public List<CommentResponseDTO> getCommentData() {
        return commentData;
    }

    public void setCommentData(List<CommentResponseDTO> commentData) {
        this.commentData = commentData;
    }
}
