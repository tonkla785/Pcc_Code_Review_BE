package pccth.code.review.Backend.DTO.Response;

import java.util.UUID;

public class ProjectSummaryDTO {

    private UUID id;
    private String name;
    private String sonarProjectKey;

    public ProjectSummaryDTO() {
    }

    public ProjectSummaryDTO(UUID id, String name, String sonarProjectKey) {
        this.id = id;
        this.name = name;
        this.sonarProjectKey = sonarProjectKey;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSonarProjectKey() {
        return sonarProjectKey;
    }

    public void setSonarProjectKey(String sonarProjectKey) {
        this.sonarProjectKey = sonarProjectKey;
    }
}
