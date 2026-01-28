package pccth.code.review.Backend.DTO.Response;

import pccth.code.review.Backend.DTO.AnalysisLogEntry;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ServiceExecutionResult {
    private UUID scanId;
    private String status;
    private Map<String, Object> data;
    private List<AnalysisLogEntry> logs;
    private Date lastSpringTimestamp;

    public ServiceExecutionResult() {
    }

    public ServiceExecutionResult(UUID scanId, String status, Map<String, Object> data, List<AnalysisLogEntry> logs) {
        this.scanId = scanId;
        this.status = status;
        this.data = data;
        this.logs = logs;
        this.lastSpringTimestamp = new Date();
    }

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

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public List<AnalysisLogEntry> getLogs() {
        return logs;
    }

    public void setLogs(List<AnalysisLogEntry> logs) {
        this.logs = logs;
    }

    public Date getLastSpringTimestamp() {
        return lastSpringTimestamp;
    }

    public void setLastSpringTimestamp(Date lastSpringTimestamp) {
        this.lastSpringTimestamp = lastSpringTimestamp;
    }
}
