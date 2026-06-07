package pccth.code.review.Backend.DTO.Response;

import java.util.List;

public class SecurityMetricsResponseDTO {

    private int score;
    private String riskLevel;                 // SAFE / LOW / MEDIUM / HIGH / CRITICAL
    private List<CountItem> vulnerabilities;   // Critical / High / Medium / Low
    private List<CountItem> hotIssues;
    private List<CountItem> owaspCoverage;

    public static class CountItem {
        private String name;
        private int count;
        private String status;

        public CountItem(String name, int count, String status) {
            this.name = name;
            this.count = count;
            this.status = status;
        }

        public String getName() {
            return name;
        }

        public int getCount() {
            return count;
        }

        public String getStatus() {
            return status;
        }
    }

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

    public List<CountItem> getVulnerabilities() {
        return vulnerabilities;
    }

    public void setVulnerabilities(List<CountItem> vulnerabilities) {
        this.vulnerabilities = vulnerabilities;
    }

    public List<CountItem> getHotIssues() {
        return hotIssues;
    }

    public void setHotIssues(List<CountItem> hotIssues) {
        this.hotIssues = hotIssues;
    }

    public List<CountItem> getOwaspCoverage() {
        return owaspCoverage;
    }

    public void setOwaspCoverage(List<CountItem> owaspCoverage) {
        this.owaspCoverage = owaspCoverage;
    }
}
