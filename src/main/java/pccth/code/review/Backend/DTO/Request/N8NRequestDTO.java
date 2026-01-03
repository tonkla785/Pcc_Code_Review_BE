package pccth.code.review.Backend.DTO.Request;

import pccth.code.review.Backend.EnumType.ProjectTypeEnum;

import java.util.UUID;

public class N8NRequestDTO {
    private UUID projectId;
    private UUID scanId;

    private String repositoryUrl;
    private String branch;

    private ProjectTypeEnum projectType;
    private String sonarProjectKey;

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
}
