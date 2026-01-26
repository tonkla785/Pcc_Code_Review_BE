package pccth.code.review.Backend.DTO.Request;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class IssueUpdateRequestDTO {

    @NotNull
    private UUID id;

    // ให้รับทั้ง status / issueStatus / state (ถ้าอยาก) ก็ alias เพิ่มได้
    @JsonAlias({"issueStatus", "state"})
    private String status;

    // รับทั้ง assignedTo / assigned_to / assigned_to_id
    @JsonAlias({"assigned_to", "assigned_to_id", "assignee", "assignee_id"})
    private UUID assignedTo;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public UUID getAssignedTo() { return assignedTo; }
    public void setAssignedTo(UUID assignedTo) { this.assignedTo = assignedTo; }
}
