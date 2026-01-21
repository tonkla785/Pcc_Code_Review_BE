package pccth.code.review.Backend.DTO;

import pccth.code.review.Backend.EnumType.ScanStatusEnum;

import java.util.UUID;


public class ScanWsEvent {
    private UUID projectId;
    private String status; // SCANNING, SUCCESS, FAILED

    // สำหรับ Jackson
    public ScanWsEvent() {
    }

    public ScanWsEvent(UUID projectId, String status) {
        this.projectId = projectId;
        this.status = status;
    }

    public UUID getProjectId() { return projectId; }
    public String getStatus() { return status; }
}
