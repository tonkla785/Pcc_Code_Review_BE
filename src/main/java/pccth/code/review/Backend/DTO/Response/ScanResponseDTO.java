package pccth.code.review.Backend.DTO.Response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import pccth.code.review.Backend.Entity.ProjectEntity;
import pccth.code.review.Backend.EnumType.ScanStatusEnum;

import java.util.*;

public class ScanResponseDTO {
    private UUID id;
    private ProjectResponseDTO project;
    private ScanStatusEnum status;
    private Date startedAt;
    private Date completedAt;
    private String qualityGate;
    private Map<String, Object> metrics;
    private String logFilePath;
    private List<IssuesReponseDTO> issueData = new ArrayList<>();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public ProjectResponseDTO getProject() {
        return project;
    }

    public void setProject(ProjectResponseDTO project) {
        this.project = project;
    }

    public ScanStatusEnum getStatus() {
        return status;
    }

    public void setStatus(ScanStatusEnum status) {
        this.status = status;
    }

    public Date getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Date startedAt) {
        this.startedAt = startedAt;
    }

    public Date getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Date completedAt) {
        this.completedAt = completedAt;
    }

    public String getQualityGate() {
        return qualityGate;
    }

    public void setQualityGate(String qualityGate) {
        this.qualityGate = qualityGate;
    }

    public Map<String, Object> getMetrics() {
        return metrics;
    }

    public void setMetrics(Map<String, Object> metrics) {
        this.metrics = metrics;
    }

    public String getLogFilePath() {
        return logFilePath;
    }

    public void setLogFilePath(String logFilePath) {
        this.logFilePath = logFilePath;
    }

    public List<IssuesReponseDTO> getIssueData() {
        return issueData;
    }

    public void setIssueData(List<IssuesReponseDTO> issueData) {
        this.issueData = issueData;
    }
}
