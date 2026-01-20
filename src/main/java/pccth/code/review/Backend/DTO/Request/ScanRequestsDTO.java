package pccth.code.review.Backend.DTO.Request;

import jakarta.validation.constraints.NotNull;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class ScanRequestsDTO {

    @NotNull(message = "Project ID cannot be null")
    private UUID projectId;

    private String status;
    private Date startedAt;
    private Date completedAt;
    private String qualityGate;
    private Map<String, Object> metrics;
    private String logFilePath;

    // Getters and Setters

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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
}


