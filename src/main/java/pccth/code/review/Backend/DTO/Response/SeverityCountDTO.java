package pccth.code.review.Backend.DTO.Response;

import java.util.UUID;

public class SeverityCountDTO {
    private UUID scanId;
    private UUID projectId;
    private String projectName;
    private String severity;
    private Long count;

    public SeverityCountDTO(UUID scanId, UUID projectId,String projectName, String severity, Long count) {
        this.scanId = scanId;
        this.projectId = projectId;
        this.projectName = projectName;
        this.severity = severity;
        this.count = count;
    }

    public SeverityCountDTO(String severity, Long count) {
        this.severity = severity;
        this.count = count;
    }

    public UUID getScanId() { return scanId; }
    public void setScanId(UUID scanId) { this.scanId = scanId; }

    public UUID getProjectId() { return projectId; }
    public void setProjectId(UUID projectId) { this.projectId = projectId; }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public Long getCount() { return count; }
    public void setCount(Long count) { this.count = count; }
}
