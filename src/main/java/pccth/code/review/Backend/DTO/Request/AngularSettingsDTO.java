package pccth.code.review.Backend.DTO.Request;

public class AngularSettingsDTO {
    private boolean runNpm;
    private boolean coverage;
    private boolean tsFiles;
    private String exclusions;

    public boolean isRunNpm() {
        return runNpm;
    }

    public void setRunNpm(boolean runNpm) {
        this.runNpm = runNpm;
    }

    public boolean isCoverage() {
        return coverage;
    }

    public void setCoverage(boolean coverage) {
        this.coverage = coverage;
    }

    public boolean isTsFiles() {
        return tsFiles;
    }

    public void setTsFiles(boolean tsFiles) {
        this.tsFiles = tsFiles;
    }

    public String getExclusions() {
        return exclusions;
    }

    public void setExclusions(String exclusions) {
        this.exclusions = exclusions;
    }
}
