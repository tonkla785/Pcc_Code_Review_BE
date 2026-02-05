package pccth.code.review.Backend.DTO.Request;

/**
 * Request DTO สำหรับ trigger scan จาก frontend
 * รวม settings ต่างๆ จาก SonarQubeConfig
 */
public class TriggerScanRequestDTO {
    private String branch;
    private String sonarToken;
    private AngularSettingsDTO angularSettings;
    private SpringSettingsDTO springSettings;
    private String gitToken;



    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getSonarToken() {
        return sonarToken;
    }

    public void setSonarToken(String sonarToken) {
        this.sonarToken = sonarToken;
    }

    public AngularSettingsDTO getAngularSettings() {
        return angularSettings;
    }

    public void setAngularSettings(AngularSettingsDTO angularSettings) {
        this.angularSettings = angularSettings;
    }

    public SpringSettingsDTO getSpringSettings() {
        return springSettings;
    }

    public void setSpringSettings(SpringSettingsDTO springSettings) {
        this.springSettings = springSettings;
    }

    public String getGitToken() { return gitToken; }

    public void setGitToken(String gitToken) { this.gitToken = gitToken; }
}
