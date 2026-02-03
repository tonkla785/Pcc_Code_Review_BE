package pccth.code.review.Backend.DTO.Request;

public class SonarQubeConfigRequestDTO {

    private String userId;
    private String serverUrl;
    private String authToken;
    private String organization;

    // Angular Settings
    private Boolean angularRunNpm;
    private Boolean angularCoverage;
    private Boolean angularTsFiles;
    private String angularExclusions;

    // Spring Settings
    private Boolean springRunTests;
    private Boolean springJacoco;
    private String springBuildTool;
    private Integer springJdkVersion;

    // Quality Gates
    private Boolean qgFailOnError;
    private Integer qgCoverageThreshold;
    private Integer qgMaxBugs;
    private Integer qgMaxVulnerabilities;
    private Integer qgMaxCodeSmells;

    // User ID getter/setter
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    // Getters and Setters
    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public Boolean getAngularRunNpm() {
        return angularRunNpm;
    }

    public void setAngularRunNpm(Boolean angularRunNpm) {
        this.angularRunNpm = angularRunNpm;
    }

    public Boolean getAngularCoverage() {
        return angularCoverage;
    }

    public void setAngularCoverage(Boolean angularCoverage) {
        this.angularCoverage = angularCoverage;
    }

    public Boolean getAngularTsFiles() {
        return angularTsFiles;
    }

    public void setAngularTsFiles(Boolean angularTsFiles) {
        this.angularTsFiles = angularTsFiles;
    }

    public String getAngularExclusions() {
        return angularExclusions;
    }

    public void setAngularExclusions(String angularExclusions) {
        this.angularExclusions = angularExclusions;
    }

    public Boolean getSpringRunTests() {
        return springRunTests;
    }

    public void setSpringRunTests(Boolean springRunTests) {
        this.springRunTests = springRunTests;
    }

    public Boolean getSpringJacoco() {
        return springJacoco;
    }

    public void setSpringJacoco(Boolean springJacoco) {
        this.springJacoco = springJacoco;
    }

    public String getSpringBuildTool() {
        return springBuildTool;
    }

    public void setSpringBuildTool(String springBuildTool) {
        this.springBuildTool = springBuildTool;
    }

    public Integer getSpringJdkVersion() {
        return springJdkVersion;
    }

    public void setSpringJdkVersion(Integer springJdkVersion) {
        this.springJdkVersion = springJdkVersion;
    }

    public Boolean getQgFailOnError() {
        return qgFailOnError;
    }

    public void setQgFailOnError(Boolean qgFailOnError) {
        this.qgFailOnError = qgFailOnError;
    }

    public Integer getQgCoverageThreshold() {
        return qgCoverageThreshold;
    }

    public void setQgCoverageThreshold(Integer qgCoverageThreshold) {
        this.qgCoverageThreshold = qgCoverageThreshold;
    }

    public Integer getQgMaxBugs() {
        return qgMaxBugs;
    }

    public void setQgMaxBugs(Integer qgMaxBugs) {
        this.qgMaxBugs = qgMaxBugs;
    }

    public Integer getQgMaxVulnerabilities() {
        return qgMaxVulnerabilities;
    }

    public void setQgMaxVulnerabilities(Integer qgMaxVulnerabilities) {
        this.qgMaxVulnerabilities = qgMaxVulnerabilities;
    }

    public Integer getQgMaxCodeSmells() {
        return qgMaxCodeSmells;
    }

    public void setQgMaxCodeSmells(Integer qgMaxCodeSmells) {
        this.qgMaxCodeSmells = qgMaxCodeSmells;
    }
}
