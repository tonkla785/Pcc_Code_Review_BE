package pccth.code.review.Backend.DTO.Request;

public class SpringSettingsDTO {
    private boolean runTests;
    private boolean jacoco;
    private String buildTool;

    public boolean isRunTests() {
        return runTests;
    }

    public void setRunTests(boolean runTests) {
        this.runTests = runTests;
    }

    public boolean isJacoco() {
        return jacoco;
    }

    public void setJacoco(boolean jacoco) {
        this.jacoco = jacoco;
    }

    public String getBuildTool() {
        return buildTool;
    }

    public void setBuildTool(String buildTool) {
        this.buildTool = buildTool;
    }
}
