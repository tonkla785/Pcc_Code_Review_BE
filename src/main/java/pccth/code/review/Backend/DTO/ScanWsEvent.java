package pccth.code.review.Backend.DTO;

import pccth.code.review.Backend.EnumType.ScanStatusEnum;

import java.util.UUID;


public class ScanWsEvent {
    private UUID projectId;
    private ScanStatusEnum status; // SCANNING, SUCCESS, FAILED

    // สำหรับ Jackson
    public ScanWsEvent() {
    }

    public ScanWsEvent(UUID projectId, ScanStatusEnum status) {
        this.projectId = projectId;
        this.status = status;
    }

    public UUID getProjectId() { return projectId; }
    public ScanStatusEnum getStatus() { return status; }
}
