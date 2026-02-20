package pccth.code.review.Backend.DTO.Response;

public class IssueDetailResponseDTO {
    private String description;
    private String vulnerableCode;
    private String recommendedFix;
    private String recommendedFixByAi;
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRecommendedFixByAi() {
        return recommendedFixByAi;
    }

    public void setRecommendedFixByAi(String recommendedFixByAi) {
        this.recommendedFixByAi = recommendedFixByAi;
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
}
