package pccth.code.review.Backend.DTO.Response;

import java.util.Date;
import java.util.UUID;

public class NotificationSettingsResponseDTO {

    private UUID id;
    private UUID userId;
    private Boolean scansEnabled;
    private Boolean issuesEnabled;
    private Boolean systemEnabled;
    private Boolean reportsEnabled;
    private Date createdAt;
    private Date updatedAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

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
