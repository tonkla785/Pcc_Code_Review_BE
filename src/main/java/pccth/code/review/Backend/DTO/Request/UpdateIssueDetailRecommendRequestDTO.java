package pccth.code.review.Backend.DTO.Request;

import java.util.UUID;

public class UpdateIssueDetailRecommendRequestDTO {
    private UUID issueId;
    private UUID projectId;
    private String recommendedFixAi;
    private String webhookToken;

    public String getWebhookToken() {
        return webhookToken;
    }

    public void setWebhookToken(String webhookToken) {
        this.webhookToken = webhookToken;
    }

    public String getRecommendedFixAi() {
        return recommendedFixAi;
    }

    public void setRecommendedFixAi(String recommendedFixAi) {
        this.recommendedFixAi = recommendedFixAi;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public UUID getIssueId() {
        return issueId;
    }

    public void setIssueId(UUID issueId) {
        this.issueId = issueId;
    }
}
