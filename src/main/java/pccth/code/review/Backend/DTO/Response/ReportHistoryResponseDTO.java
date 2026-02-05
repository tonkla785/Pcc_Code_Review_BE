package pccth.code.review.Backend.DTO.Response;

import java.time.LocalDate;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class ReportHistoryResponseDTO {

    private UUID id;
    private UUID userId;
    private UUID projectId;
    private String projectName;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private String format;
    private String generatedBy;
    private Date generatedAt;

    // Selected sections
    private Boolean includeQualityGate;
    private Boolean includeIssueBreakdown;
    private Boolean includeSecurityAnalysis;
    private Boolean includeTechnicalDebt;
    private Boolean includeRecommendations;

    // Snapshot data
    private Map<String, Object> snapshotData;

    private Long fileSizeBytes;

    // Getters and Setters

    public Boolean getIncludeTechnicalDebt() {
        return includeTechnicalDebt;
    }

    public void setIncludeTechnicalDebt(Boolean includeTechnicalDebt) {
        this.includeTechnicalDebt = includeTechnicalDebt;
    }

    public Boolean getIncludeRecommendations() {
        return includeRecommendations;
    }

    public void setIncludeRecommendations(Boolean includeRecommendations) {
        this.includeRecommendations = includeRecommendations;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public LocalDate getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(LocalDate dateFrom) {
        this.dateFrom = dateFrom;
    }

    public LocalDate getDateTo() {
        return dateTo;
    }

    public void setDateTo(LocalDate dateTo) {
        this.dateTo = dateTo;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getGeneratedBy() {
        return generatedBy;
    }

    public void setGeneratedBy(String generatedBy) {
        this.generatedBy = generatedBy;
    }

    public Date getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(Date generatedAt) {
        this.generatedAt = generatedAt;
    }

    public Boolean getIncludeQualityGate() {
        return includeQualityGate;
    }

    public void setIncludeQualityGate(Boolean includeQualityGate) {
        this.includeQualityGate = includeQualityGate;
    }

    public Boolean getIncludeIssueBreakdown() {
        return includeIssueBreakdown;
    }

    public void setIncludeIssueBreakdown(Boolean includeIssueBreakdown) {
        this.includeIssueBreakdown = includeIssueBreakdown;
    }

    public Boolean getIncludeSecurityAnalysis() {
        return includeSecurityAnalysis;
    }

    public void setIncludeSecurityAnalysis(Boolean includeSecurityAnalysis) {
        this.includeSecurityAnalysis = includeSecurityAnalysis;
    }

    public Map<String, Object> getSnapshotData() {
        return snapshotData;
    }

    public void setSnapshotData(Map<String, Object> snapshotData) {
        this.snapshotData = snapshotData;
    }

    public Long getFileSizeBytes() {
        return fileSizeBytes;
    }

    public void setFileSizeBytes(Long fileSizeBytes) {
        this.fileSizeBytes = fileSizeBytes;
    }
}
