package pccth.code.review.Backend.DTO;

import pccth.code.review.Backend.EnumType.ScanStatusEnum;

import java.util.UUID;


public class ScanWsEvent {
    private UUID projectId;
    private ScanStatusEnum status; // SCANNING, SUCCESS, FAILED
    private UUID scanId;

    // สำหรับ Jackson
    public ScanWsEvent() {
    }

    public ScanWsEvent(UUID projectId, ScanStatusEnum status, UUID scanId) {
        this.projectId = projectId;
        this.status = status;
        this.scanId = scanId;
    }

    public UUID getScanId() { return scanId; }
    public UUID getProjectId() { return projectId; }
    public ScanStatusEnum getStatus() { return status; }
}
