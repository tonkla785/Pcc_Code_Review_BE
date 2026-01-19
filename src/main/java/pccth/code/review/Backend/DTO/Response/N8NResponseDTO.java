package pccth.code.review.Backend.DTO.Response;

import pccth.code.review.Backend.DTO.SonarMetricsDTO;
import pccth.code.review.Backend.EnumType.ScanStatusEnum;

import java.util.Date;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class N8NResponseDTO {
    private UUID scanId;
    private UUID projectId;
    private ScanStatusEnum status;
    private String qualityGate;
    private Long analysisDuration;
    private Date analyzedAt;
    private SonarMetricsDTO metrics;
    private String errorMessage;
    private String logFilePath;
    private String markDown;

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public String getMarkDown() {
        return markDown;
    }

    public void setMarkDown(String markDown) {
        this.markDown = markDown;
    }

    public UUID getScanId() {
        return scanId;
    }

    public void setScanId(UUID scanId) {
        this.scanId = scanId;
    }

    public ScanStatusEnum getStatus() {
        return status;
    }

    public void setStatus(ScanStatusEnum status) {
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

    public Date getAnalyzedAt() {
        return analyzedAt;
    }

    public void setAnalyzedAt(Date analyzedAt) {
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


    // for test result from n8n
    @Override
    public String toString() {
        return """
                N8NResponseDTO {
                  scanId           = %s
                  projectId        = %s
                  status           = %s
                  qualityGate      = %s
                  analysisDuration = %d ms
                  analyzedAt       = %s
                  metrics          = %s
                  errorMessage     = %s
                  logFilePath      = %s
                  markDown         = %s
                }
                """.formatted(
                scanId,
                projectId,
                status,
                qualityGate,
                analysisDuration,
                analyzedAt,
                metrics,
                errorMessage,
                logFilePath,
                markDown);
    }

}
