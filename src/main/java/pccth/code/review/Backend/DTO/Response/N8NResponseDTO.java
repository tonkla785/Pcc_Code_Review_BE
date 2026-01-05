package pccth.code.review.Backend.DTO.Response;

import pccth.code.review.Backend.DTO.SonarMetricsDTO;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class N8NResponseDTO {
    private UUID scanId;
    private String status;
    private String qualityGate;
    private Long analysisDuration;
    private Instant analyzedAt;
    private SonarMetricsDTO metrics;
    private String errorMessage;
    private String logFilePath;

    public UUID getScanId() {
        return scanId;
    }

    public void setScanId(UUID scanId) {
        this.scanId = scanId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getQualityGate() {
        return qualityGate;
    }

    public void setQualityGate(String qualityGate) {
        this.qualityGate = qualityGate;
    }

    public Long getAnalysisDuration() {
        return analysisDuration;
    }

    public void setAnalysisDuration(Long analysisDuration) {
        this.analysisDuration = analysisDuration;
    }

    public Instant getAnalyzedAt() {
        return analyzedAt;
    }

    public void setAnalyzedAt(Instant analyzedAt) {
        this.analyzedAt = analyzedAt;
    }

    public SonarMetricsDTO getMetrics() {
        return metrics;
    }

    public void setMetrics(SonarMetricsDTO metrics) {
        this.metrics = metrics;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getLogFilePath() {
        return logFilePath;
    }

    public void setLogFilePath(String logFilePath) {
        this.logFilePath = logFilePath;
    }
}
