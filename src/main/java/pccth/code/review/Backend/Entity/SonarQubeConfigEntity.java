package pccth.code.review.Backend.Entity;

import jakarta.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "sonarqube_config")
public class SonarQubeConfigEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid")
    private UUID id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserEntity user;

    @Column(name = "server_url", length = 500)
    private String serverUrl;

    @Column(name = "auth_token", length = 500)
    private String authToken;

    @Column(name = "organization", length = 255)
    private String organization;

    // Angular Settings
    @Column(name = "angular_run_npm")
    private Boolean angularRunNpm = false;

    @Column(name = "angular_coverage")
    private Boolean angularCoverage = false;

    @Column(name = "angular_ts_files")
    private Boolean angularTsFiles = false;

    @Column(name = "angular_exclusions", columnDefinition = "TEXT")
    private String angularExclusions;

    // Spring Settings
    @Column(name = "spring_run_tests")
    private Boolean springRunTests = false;

    @Column(name = "spring_jacoco")
    private Boolean springJacoco = false;

    @Column(name = "spring_build_tool", length = 50)
    private String springBuildTool;

    @Column(name = "spring_jdk_version")
    private Integer springJdkVersion;

    // Quality Gates
    @Column(name = "qg_fail_on_error")
    private Boolean qgFailOnError = false;

    @Column(name = "qg_coverage_threshold")
    private Integer qgCoverageThreshold = 0;

    @Column(name = "qg_max_bugs")
    private Integer qgMaxBugs = 0;

    @Column(name = "qg_max_vulnerabilities")
    private Integer qgMaxVulnerabilities = 0;

    @Column(name = "qg_max_code_smells")
    private Integer qgMaxCodeSmells = 0;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;

    // Getters and Setters
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
