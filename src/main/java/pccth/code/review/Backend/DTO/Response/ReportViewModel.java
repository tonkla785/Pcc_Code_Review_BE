package pccth.code.review.Backend.DTO.Response;

import java.util.ArrayList;
import java.util.List;

public class ReportViewModel {

    private String projectName;
    private String dateFrom;
    private String dateTo;
    private String generatedDate;
    private String generatedBy;

    private boolean showQualityGate;
    private boolean showIssueBreakdown;
    private boolean showSecurityAnalysis;
    private boolean showTechnicalDebt;
    private boolean showRecommendations;

    private QualityGate qualityGate = new QualityGate();
    private List<IssueRow> issues = new ArrayList<>();
    private Security security = new Security();
    private List<DebtRow> technicalDebt = new ArrayList<>();
    private List<RecRow> recommendations = new ArrayList<>();

    public static class QualityGate {
        private int totalScans;
        private int passed;
        private int failed;
        private List<ScanRow> scans = new ArrayList<>();

        public int getTotalScans() {
            return totalScans;
        }

        public void setTotalScans(int totalScans) {
            this.totalScans = totalScans;
        }

        public int getPassed() {
            return passed;
        }

        public void setPassed(int passed) {
            this.passed = passed;
        }

        public int getFailed() {
            return failed;
        }

        public void setFailed(int failed) {
            this.failed = failed;
        }

        public List<ScanRow> getScans() {
            return scans;
        }

        public void setScans(List<ScanRow> scans) {
            this.scans = scans;
        }
    }

    public static class ScanRow {
        private String date;
        private String status;
        private String qualityGate;
        private String reliability;
        private String security;
        private String maintainability;
        private int bugs;
        private int vulnerabilities;
        private int codeSmells;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
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

        public String getReliability() {
            return reliability;
        }

        public void setReliability(String reliability) {
            this.reliability = reliability;
        }

        public String getSecurity() {
            return security;
        }

        public void setSecurity(String security) {
            this.security = security;
        }

        public String getMaintainability() {
            return maintainability;
        }

        public void setMaintainability(String maintainability) {
            this.maintainability = maintainability;
        }

        public int getBugs() {
            return bugs;
        }

        public void setBugs(int bugs) {
            this.bugs = bugs;
        }

        public int getVulnerabilities() {
            return vulnerabilities;
        }

        public void setVulnerabilities(int vulnerabilities) {
            this.vulnerabilities = vulnerabilities;
        }

        public int getCodeSmells() {
            return codeSmells;
        }

        public void setCodeSmells(int codeSmells) {
            this.codeSmells = codeSmells;
        }
    }

    public static class IssueRow {
        private String type;
        private String severity;
        private String component;
        private String line;
        private String message;

        public IssueRow() {
        }

        public IssueRow(String type, String severity, String component, String line, String message) {
            this.type = type;
            this.severity = severity;
            this.component = component;
            this.line = line;
            this.message = message;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getSeverity() {
            return severity;
        }

        public void setSeverity(String severity) {
            this.severity = severity;
        }

        public String getComponent() {
            return component;
        }

        public void setComponent(String component) {
            this.component = component;
        }

        public String getLine() {
            return line;
        }

        public void setLine(String line) {
            this.line = line;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public static class Security {
        private int score;
        private String riskLevel;
        private List<NameCount> vulnerabilities = new ArrayList<>();
        private List<NameCount> hotIssues = new ArrayList<>();
        private List<OwaspRow> owasp = new ArrayList<>();

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public String getRiskLevel() {
            return riskLevel;
        }

        public void setRiskLevel(String riskLevel) {
            this.riskLevel = riskLevel;
        }

        public List<NameCount> getVulnerabilities() {
            return vulnerabilities;
        }

        public void setVulnerabilities(List<NameCount> vulnerabilities) {
            this.vulnerabilities = vulnerabilities;
        }

        public List<NameCount> getHotIssues() {
            return hotIssues;
        }

        public void setHotIssues(List<NameCount> hotIssues) {
            this.hotIssues = hotIssues;
        }

        public List<OwaspRow> getOwasp() {
            return owasp;
        }

        public void setOwasp(List<OwaspRow> owasp) {
            this.owasp = owasp;
        }
    }

    public static class NameCount {
        private String name;
        private int count;

        public NameCount() {
        }

        public NameCount(String name, int count) {
            this.name = name;
            this.count = count;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }

    public static class OwaspRow {
        private String name;
        private int count;
        private String status; // Pass / Warning / Fail

        public OwaspRow() {
        }

        public OwaspRow(String name, int count, String status) {
            this.name = name;
            this.count = count;
            this.status = status;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    public static class DebtRow {
        private String date;
        private String project;
        private String debt;
        private String cost;

        public DebtRow() {
        }

        public DebtRow(String date, String project, String debt, String cost) {
            this.date = date;
            this.project = project;
            this.debt = debt;
            this.cost = cost;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getProject() {
            return project;
        }

        public void setProject(String project) {
            this.project = project;
        }

        public String getDebt() {
            return debt;
        }

        public void setDebt(String debt) {
            this.debt = debt;
        }

        public String getCost() {
            return cost;
        }

        public void setCost(String cost) {
            this.cost = cost;
        }
    }

    public static class RecRow {
        private String severity;
        private String type;
        private String component;
        private String line;
        private String message;
        private String recommendedFix;

        public RecRow() {
        }

        public RecRow(String severity, String type, String component, String line,
                      String message, String recommendedFix) {
            this.severity = severity;
            this.type = type;
            this.component = component;
            this.line = line;
            this.message = message;
            this.recommendedFix = recommendedFix;
        }

        public String getSeverity() {
            return severity;
        }

        public void setSeverity(String severity) {
            this.severity = severity;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getComponent() {
            return component;
        }

        public void setComponent(String component) {
            this.component = component;
        }

        public String getLine() {
            return line;
        }

        public void setLine(String line) {
            this.line = line;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getRecommendedFix() {
            return recommendedFix;
        }

        public void setRecommendedFix(String recommendedFix) {
            this.recommendedFix = recommendedFix;
        }
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(String dateFrom) {
        this.dateFrom = dateFrom;
    }

    public String getDateTo() {
        return dateTo;
    }

    public void setDateTo(String dateTo) {
        this.dateTo = dateTo;
    }

    public String getGeneratedDate() {
        return generatedDate;
    }

    public void setGeneratedDate(String generatedDate) {
        this.generatedDate = generatedDate;
    }

    public String getGeneratedBy() {
        return generatedBy;
    }

    public void setGeneratedBy(String generatedBy) {
        this.generatedBy = generatedBy;
    }

    public boolean isShowQualityGate() {
        return showQualityGate;
    }

    public void setShowQualityGate(boolean showQualityGate) {
        this.showQualityGate = showQualityGate;
    }

    public boolean isShowIssueBreakdown() {
        return showIssueBreakdown;
    }

    public void setShowIssueBreakdown(boolean showIssueBreakdown) {
        this.showIssueBreakdown = showIssueBreakdown;
    }

    public boolean isShowSecurityAnalysis() {
        return showSecurityAnalysis;
    }

    public void setShowSecurityAnalysis(boolean showSecurityAnalysis) {
        this.showSecurityAnalysis = showSecurityAnalysis;
    }

    public boolean isShowTechnicalDebt() {
        return showTechnicalDebt;
    }

    public void setShowTechnicalDebt(boolean showTechnicalDebt) {
        this.showTechnicalDebt = showTechnicalDebt;
    }

    public boolean isShowRecommendations() {
        return showRecommendations;
    }

    public void setShowRecommendations(boolean showRecommendations) {
        this.showRecommendations = showRecommendations;
    }

    public QualityGate getQualityGate() {
        return qualityGate;
    }

    public void setQualityGate(QualityGate qualityGate) {
        this.qualityGate = qualityGate;
    }

    public List<IssueRow> getIssues() {
        return issues;
    }

    public void setIssues(List<IssueRow> issues) {
        this.issues = issues;
    }

    public Security getSecurity() {
        return security;
    }

    public void setSecurity(Security security) {
        this.security = security;
    }

    public List<DebtRow> getTechnicalDebt() {
        return technicalDebt;
    }

    public void setTechnicalDebt(List<DebtRow> technicalDebt) {
        this.technicalDebt = technicalDebt;
    }

    public List<RecRow> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<RecRow> recommendations) {
        this.recommendations = recommendations;
    }
}
