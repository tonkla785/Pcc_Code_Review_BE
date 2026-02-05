package pccth.code.review.Backend.Entity;

import jakarta.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "notification_settings")
public class NotificationSettingsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid")
    private UUID id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserEntity user;

    @Column(name = "scans_enabled")
    private Boolean scansEnabled = true;

    @Column(name = "issues_enabled")
    private Boolean issuesEnabled = true;

    @Column(name = "system_enabled")
    private Boolean systemEnabled = true;

    @Column(name = "reports_enabled")
    private Boolean reportsEnabled = true;

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
