package pccth.code.review.Backend.Entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "report_history")
public class ReportHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private ProjectEntity project;

    @Column(name = "project_name", nullable = false, length = 255)
    private String projectName;

    @Column(name = "date_from", nullable = false)
    private LocalDate dateFrom;

    @Column(name = "date_to", nullable = false)
    private LocalDate dateTo;

    @Column(name = "format", nullable = false, length = 20)
    private String format; // 'PDF', 'Excel', 'Word', 'PowerPoint'

    @Column(name = "generated_by", length = 100)
    private String generatedBy;

    @Column(name = "generated_at")
    private Date generatedAt;

    // Selected sections
    @Column(name = "include_quality_gate")
    private Boolean includeQualityGate = false;

    @Column(name = "include_issue_breakdown")
    private Boolean includeIssueBreakdown = false;

    @Column(name = "include_security_analysis")
    private Boolean includeSecurityAnalysis = false;

    @Column(name = "include_technical_debt")
    private Boolean includeTechnicalDebt = false;

    @Column(name = "include_recommendations")
    private Boolean includeRecommendations =false;

    // Snapshot data (JSONB)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "snapshot_data", columnDefinition = "jsonb")
    private Map<String, Object> snapshotData;

    @Column(name = "file_size_bytes")
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

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public ProjectEntity getProject() {
        return project;
    }

    public void setProject(ProjectEntity project) {
        this.project = project;
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
