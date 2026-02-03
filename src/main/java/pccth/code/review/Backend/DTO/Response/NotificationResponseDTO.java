package pccth.code.review.Backend.DTO.Response;

import java.util.Date;
import java.util.UUID;

public class NotificationResponseDTO {

    private UUID id;
    private UUID userId;
    private String type;
    private String title;
    private String message;
    private Boolean isRead;
    private Date createdAt;

    // Related IDs
    private UUID relatedProjectId;
    private UUID relatedScanId;
    private UUID relatedIssueId;
    private UUID relatedCommentId;

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
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

    public UUID getRelatedProjectId() {
        return relatedProjectId;
    }

    public void setRelatedProjectId(UUID relatedProjectId) {
        this.relatedProjectId = relatedProjectId;
    }

    public UUID getRelatedScanId() {
        return relatedScanId;
    }

    public void setRelatedScanId(UUID relatedScanId) {
        this.relatedScanId = relatedScanId;
    }

    public UUID getRelatedIssueId() {
        return relatedIssueId;
    }

    public void setRelatedIssueId(UUID relatedIssueId) {
        this.relatedIssueId = relatedIssueId;
    }

    public UUID getRelatedCommentId() {
        return relatedCommentId;
    }

    public void setRelatedCommentId(UUID relatedCommentId) {
        this.relatedCommentId = relatedCommentId;
    }
}
