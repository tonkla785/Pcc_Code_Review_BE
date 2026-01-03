package pccth.code.review.Backend.DTO;

import pccth.code.review.Backend.EnumType.ScanStatusEnum;

import java.util.Map;
import java.util.UUID;

public class N8NResultDTO {
    private UUID scanId;
    private ScanStatusEnum status; // SUCCESS | FAILED
    private String qualityGate;
    private Map<String, Object> metrics;
    private String logFilePath;

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
