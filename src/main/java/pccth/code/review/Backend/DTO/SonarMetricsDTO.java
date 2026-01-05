package pccth.code.review.Backend.DTO;

public class SonarMetricsDTO {
    private Integer bugs;
    private String reliabilityRating;

    private Integer vulnerabilities;
    private Integer securityHotspots;
    private String securityRating;

    private Integer codeSmells;
    private String maintainabilityRating;

    private Double coverage;
    private Double duplicatedLinesDensity;

    private Long technicalDebtMinutes;
    private Double debtRatio;

    public Integer getBugs() {
        return bugs;
    }

    public void setBugs(Integer bugs) {
        this.bugs = bugs;
    }

    public String getReliabilityRating() {
        return reliabilityRating;
    }

    public void setReliabilityRating(String reliabilityRating) {
        this.reliabilityRating = reliabilityRating;
    }

    public Integer getVulnerabilities() {
        return vulnerabilities;
    }

    public void setVulnerabilities(Integer vulnerabilities) {
        this.vulnerabilities = vulnerabilities;
    }

    public Integer getSecurityHotspots() {
        return securityHotspots;
    }

    public void setSecurityHotspots(Integer securityHotspots) {
        this.securityHotspots = securityHotspots;
    }

    public String getSecurityRating() {
        return securityRating;
    }

    public void setSecurityRating(String securityRating) {
        this.securityRating = securityRating;
    }

    public Integer getCodeSmells() {
        return codeSmells;
    }

    public void setCodeSmells(Integer codeSmells) {
        this.codeSmells = codeSmells;
    }

    public String getMaintainabilityRating() {
        return maintainabilityRating;
    }

    public void setMaintainabilityRating(String maintainabilityRating) {
        this.maintainabilityRating = maintainabilityRating;
    }

    public Double getCoverage() {
        return coverage;
    }

    public void setCoverage(Double coverage) {
        this.coverage = coverage;
    }

    public Double getDuplicatedLinesDensity() {
        return duplicatedLinesDensity;
    }

    public void setDuplicatedLinesDensity(Double duplicatedLinesDensity) {
        this.duplicatedLinesDensity = duplicatedLinesDensity;
    }

    public Long getTechnicalDebtMinutes() {
        return technicalDebtMinutes;
    }

    public void setTechnicalDebtMinutes(Long technicalDebtMinutes) {
        this.technicalDebtMinutes = technicalDebtMinutes;
    }

    public Double getDebtRatio() {
        return debtRatio;
    }

    public void setDebtRatio(Double debtRatio) {
        this.debtRatio = debtRatio;
    }
}
