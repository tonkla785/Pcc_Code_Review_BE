package pccth.code.review.Backend.DTO.Request;

import pccth.code.review.Backend.EnumType.ProjectTypeEnum;

import java.util.UUID;

public class N8NRequestDTO {
    private UUID projectId;
    private UUID scanId;
    private String sonarToken;

    private String gitToken;

    // Token สำหรับ n8n authen กลับมา Spring Boot
    private String webhookToken;

    private String repositoryUrl;
    private String branch;

    private ProjectTypeEnum projectType;
    private String sonarProjectKey;

    // Settings จาก SonarQubeConfig
    private AngularSettingsDTO angularSettings;
    private SpringSettingsDTO springSettings;

    public String getSonarToken() {
        return sonarToken;
    }

    public void setSonarToken(String sonarToken) {
        this.sonarToken = sonarToken;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public UUID getScanId() {
        return scanId;
    }

    public void setScanId(UUID scanId) {
        this.scanId = scanId;
    }

    public String getRepositoryUrl() {
        return repositoryUrl;
    }

    public void setRepositoryUrl(String repositoryUrl) {
        this.repositoryUrl = repositoryUrl;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public ProjectTypeEnum getProjectType() {
        return projectType;
    }

    public void setProjectType(ProjectTypeEnum projectType) {
        this.projectType = projectType;
    }

    public String getSonarProjectKey() {
        return sonarProjectKey;
    }

    public void setSonarProjectKey(String sonarProjectKey) {
        this.sonarProjectKey = sonarProjectKey;
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

    public String getWebhookToken() {
        return webhookToken;
    }

    public void setWebhookToken(String webhookToken) {
        this.webhookToken = webhookToken;
    }

    public String getGitToken() {return gitToken; }

    public void setGitToken(String gitToken) { this.gitToken = gitToken; }
}
