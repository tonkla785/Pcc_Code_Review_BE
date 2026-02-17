package pccth.code.review.Backend.DTO.Response;

import java.util.UUID;

public class RecommendFixByAiResponseDTO {
    private UUID issueId;
    private String message;

    public UUID getIssueId() {
        return issueId;
    }

    public void setIssueId(UUID issueId) {
        this.issueId = issueId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
