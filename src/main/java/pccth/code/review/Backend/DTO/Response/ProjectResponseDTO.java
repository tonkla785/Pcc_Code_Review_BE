package pccth.code.review.Backend.DTO.Response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import pccth.code.review.Backend.EnumType.ProjectTypeEnum;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ProjectResponseDTO {

    private UUID id;
    private String name;
    private String repositoryUrl;
    private ProjectTypeEnum projectType;
    private String sonarProjectKey;
    private BigDecimal costPerDay;
    private Date createdAt;
    private Date updatedAt;

    @JsonIgnoreProperties("project")
    private List<ScanResponseDTO> scanData = new ArrayList<>();

    public BigDecimal getCostPerDay() {
        return costPerDay;
    }

    public void setCostPerDay(BigDecimal costPerDay) {
        this.costPerDay = costPerDay;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRepositoryUrl() {
        return repositoryUrl;
    }

    public void setRepositoryUrl(String repositoryUrl) {
        this.repositoryUrl = repositoryUrl;
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<ScanResponseDTO> getScanData() {
        return scanData;
    }

    public void setScanData(List<ScanResponseDTO> scanData) {
        this.scanData = scanData;
    }
}
