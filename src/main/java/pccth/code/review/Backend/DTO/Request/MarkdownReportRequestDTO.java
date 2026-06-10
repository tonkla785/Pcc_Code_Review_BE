package pccth.code.review.Backend.DTO.Request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class MarkdownReportRequestDTO {

    @NotNull
    private UUID projectId;

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }
}
