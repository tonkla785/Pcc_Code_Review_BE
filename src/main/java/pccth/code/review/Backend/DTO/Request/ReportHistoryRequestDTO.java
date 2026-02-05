package pccth.code.review.Backend.DTO.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

public class ReportHistoryRequestDTO {

    @NotNull
    private UUID projectId;

    @NotBlank
    private String projectName;

    @NotNull
    private LocalDate dateFrom;

    @NotNull
    private LocalDate dateTo;

    @NotBlank
    private String format; // 'PDF', 'Excel', 'Word', 'PowerPoint'

    private String generatedBy;

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
