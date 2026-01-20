package pccth.code.review.Backend.DTO.Request;

public class SonarTestConnectRequestDTO {
    private String sonarHostUrl;
    private String sonarToken;

    public String getSonarHostUrl() {
        return sonarHostUrl;
    }

    public void setSonarHostUrl(String sonarHostUrl) {
        this.sonarHostUrl = sonarHostUrl;
    }

    public String getSonarToken() {
        return sonarToken;
    }

    public void setSonarToken(String sonarToken) {
        this.sonarToken = sonarToken;
    }
}
