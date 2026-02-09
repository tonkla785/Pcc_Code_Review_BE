package pccth.code.review.Backend.DTO.Request;

public class QualityGateSettingsDTO {
    private Boolean failOnError;          // default true
    private Integer coverageThreshold;    // default 0
    private Integer maxBugs;              // default 999999
    private Integer maxVulnerabilities;   // default 999999
    private Integer maxCodeSmells;        // default 999999

    public Boolean getFailOnError() { return failOnError; }
    public void setFailOnError(Boolean failOnError) { this.failOnError = failOnError; }

    public Integer getCoverageThreshold() { return coverageThreshold; }
    public void setCoverageThreshold(Integer coverageThreshold) { this.coverageThreshold = coverageThreshold; }

    public Integer getMaxBugs() { return maxBugs; }
    public void setMaxBugs(Integer maxBugs) { this.maxBugs = maxBugs; }

    public Integer getMaxVulnerabilities() { return maxVulnerabilities; }
    public void setMaxVulnerabilities(Integer maxVulnerabilities) { this.maxVulnerabilities = maxVulnerabilities; }

    public Integer getMaxCodeSmells() { return maxCodeSmells; }
    public void setMaxCodeSmells(Integer maxCodeSmells) { this.maxCodeSmells = maxCodeSmells; }
}
