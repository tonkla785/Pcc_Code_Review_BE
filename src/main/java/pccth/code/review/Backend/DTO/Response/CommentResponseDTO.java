package pccth.code.review.Backend.DTO.Response;

import java.util.Date;
import java.util.UUID;

public class CommentResponseDTO {
    private UUID id;
    private UUID issue;
    private UserResponseDTO user;
    private String comment;
    private UUID parentCommentId; // For reply functionality
    private Date createdAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getIssue() {
        return issue;
    }

    public void setIssue(UUID issue) {
        this.issue = issue;
    }

    public UserResponseDTO getUser() {
        return user;
    }

    public void setUser(UserResponseDTO user) {
        this.user = user;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public UUID getParentCommentId() {
        return parentCommentId;
    }

    public void setParentCommentId(UUID parentCommentId) {
        this.parentCommentId = parentCommentId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
