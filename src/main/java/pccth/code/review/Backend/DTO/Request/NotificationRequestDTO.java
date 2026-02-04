package pccth.code.review.Backend.DTO.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class NotificationRequestDTO {

    @NotNull
    private UUID userId;

    @NotBlank
    private String type; // 'Scans', 'Issues', 'System', 'Reports'

    private String title;

    @NotBlank
    private String message;

    private UUID relatedProjectId;
    private UUID relatedScanId;
    private UUID relatedIssueId;
    private UUID relatedCommentId;

    // Getters and Setters
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
