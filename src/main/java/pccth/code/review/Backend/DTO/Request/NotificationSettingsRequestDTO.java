package pccth.code.review.Backend.DTO.Request;

public class NotificationSettingsRequestDTO {

<<<<<<< HEAD
    private String userId;
=======
>>>>>>> main
    private Boolean scansEnabled;
    private Boolean issuesEnabled;
    private Boolean systemEnabled;
    private Boolean reportsEnabled;

<<<<<<< HEAD
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

=======
>>>>>>> main
    public Boolean getScansEnabled() {
        return scansEnabled;
    }

    public void setScansEnabled(Boolean scansEnabled) {
        this.scansEnabled = scansEnabled;
    }

    public Boolean getIssuesEnabled() {
        return issuesEnabled;
    }

    public void setIssuesEnabled(Boolean issuesEnabled) {
        this.issuesEnabled = issuesEnabled;
    }

    public Boolean getSystemEnabled() {
        return systemEnabled;
    }

    public void setSystemEnabled(Boolean systemEnabled) {
        this.systemEnabled = systemEnabled;
    }

    public Boolean getReportsEnabled() {
        return reportsEnabled;
    }

    public void setReportsEnabled(Boolean reportsEnabled) {
        this.reportsEnabled = reportsEnabled;
    }
}
