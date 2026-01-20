package pccth.code.review.Backend.DTO.Response;

import java.util.UUID;

public class RepositoryResponseDTO {
    private String message;
    private UUID projectId;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }
}
