package pccth.code.review.Backend.DTO.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class CommentRequestDTO {
    @NotNull(message = "User ID is required")
    private UUID userId;

    @NotBlank(message = "Comment cannot be empty")
    private String comment;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
