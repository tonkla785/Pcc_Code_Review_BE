package pccth.code.review.Backend.DTO.Request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public class ReportGenerateRequestDTO {

    @NotNull
    private UUID projectId;

    private LocalDate dateFrom;

    private LocalDate dateTo;

    private String format = "pdf";

    private Sections sections = new Sections();

    private UUID userId;

    private String generatedBy;

    public static class Sections {
        private boolean qualityGate;
        private boolean issueBreakdown;
        private boolean securityAnalysis;
        private boolean technicalDebt;
        private boolean recommendations;

        public boolean hasAny() {
            return qualityGate || issueBreakdown || securityAnalysis || technicalDebt || recommendations;
        }

        public static Sections all() {
            Sections s = new Sections();
            s.qualityGate = true;
            s.issueBreakdown = true;
            s.securityAnalysis = true;
            s.technicalDebt = true;
            s.recommendations = true;
            return s;
        }

        public boolean isQualityGate() {
            return qualityGate;
        }

        public void setQualityGate(boolean qualityGate) {
            this.qualityGate = qualityGate;
        }

        public boolean isIssueBreakdown() {
            return issueBreakdown;
        }

        public void setIssueBreakdown(boolean issueBreakdown) {
            this.issueBreakdown = issueBreakdown;
        }

        public boolean isSecurityAnalysis() {
            return securityAnalysis;
        }

        public void setSecurityAnalysis(boolean securityAnalysis) {
            this.securityAnalysis = securityAnalysis;
        }

        public boolean isTechnicalDebt() {
            return technicalDebt;
        }

        public void setTechnicalDebt(boolean technicalDebt) {
            this.technicalDebt = technicalDebt;
        }

        public boolean isRecommendations() {
            return recommendations;
        }

        public void setRecommendations(boolean recommendations) {
            this.recommendations = recommendations;
        }
    }

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
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

    public Sections getSections() {
        return sections == null ? new Sections() : sections;
    }

    public void setSections(Sections sections) {
        this.sections = sections;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getGeneratedBy() {
        return generatedBy;
    }

    public void setGeneratedBy(String generatedBy) {
        this.generatedBy = generatedBy;
    }
}
