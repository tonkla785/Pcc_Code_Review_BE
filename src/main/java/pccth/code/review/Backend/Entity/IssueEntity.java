package pccth.code.review.Backend.Entity;

import jakarta.persistence.*;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;


@Entity
@Table(name = "issues")
public class IssueEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "issue_key", nullable = false, length = 255,unique = true)
    private String issueKey;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private ProjectEntity project;

    @Column(name = "type", length = 50)
    private String type;

    @Column(name = "severity", length = 20)
    private String severity;

    @Column(name = "rule_key", length = 100)
    private String ruleKey;

    @Column(name = "component")
    private String component;

    @Column(name = "line")
    private Integer line;

    @Column(name = "message")
    private String message;

    @Column(name = "status", length = 50)
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to")
    private UserEntity assignedTo;

    @Column(name = "created_at")
    private Date createdAt;

    @OneToOne(
            mappedBy = "issue",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private IssueDetailEntity detail;

    @OneToMany(
            mappedBy = "issue",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ScanIssueEntity> scanIssues = new ArrayList<>();

    @OneToMany(
            mappedBy = "issue",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<CommentEntity> commentData = new ArrayList<>();

    public ProjectEntity getProject() {
        return project;
    }

    public void setProject(ProjectEntity project) {
        this.project = project;
    }

    public IssueDetailEntity getDetail() {
        return detail;
    }

    public void setDetail(IssueDetailEntity detail) {
        this.detail = detail;
    }

    public List<ScanIssueEntity> getScanIssues() {
        return scanIssues;
    }

    public void setScanIssues(List<ScanIssueEntity> scanIssues) {
        this.scanIssues = scanIssues;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public UserEntity getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(UserEntity assignedTo) {
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

    public List<CommentEntity> getCommentData() {
        return commentData;
    }

    public void setCommentData(List<CommentEntity> commentData) {
        this.commentData = commentData;
    }

    public Integer getLine() {
        return line;
    }

    public void setLine(Integer line) {
        this.line = line;
    }

    public String getRuleKey() {
        return ruleKey;
    }

    public void setRuleKey(String ruleKey) {
        this.ruleKey = ruleKey;
    }

}
