package pccth.code.review.Backend.Entity;

import jakarta.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "notifications")
public class NotificationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "type", nullable = false, length = 50)
    private String type; // 'Scans', 'Issues', 'System', 'Reports'

    @Column(name = "title", length = 255)
    private String title;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "is_read")
    private Boolean isRead = false;

    @Column(name = "created_at")
    private Date createdAt;

    // Related entities (optional)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_project_id")
    private ProjectEntity relatedProject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_scan_id")
    private ScanEntity relatedScan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_issue_id")
    private IssueEntity relatedIssue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_comment_id")
    private CommentEntity relatedComment;

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public ProjectEntity getRelatedProject() {
        return relatedProject;
    }

    public void setRelatedProject(ProjectEntity relatedProject) {
        this.relatedProject = relatedProject;
    }

    public ScanEntity getRelatedScan() {
        return relatedScan;
    }

    public void setRelatedScan(ScanEntity relatedScan) {
        this.relatedScan = relatedScan;
    }

    public IssueEntity getRelatedIssue() {
        return relatedIssue;
    }

    public void setRelatedIssue(IssueEntity relatedIssue) {
        this.relatedIssue = relatedIssue;
    }

    public CommentEntity getRelatedComment() {
        return relatedComment;
    }

    public void setRelatedComment(CommentEntity relatedComment) {
        this.relatedComment = relatedComment;
    }
}
