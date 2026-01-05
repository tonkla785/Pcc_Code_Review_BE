package pccth.code.review.Backend.DTO.Response;

import pccth.code.review.Backend.EnumType.ScanStatusEnum;

import java.util.UUID;

public class N8NScanQueueResposneDTO {
    private UUID scanId;
    private ScanStatusEnum status;

    public UUID getScanId() {
        return scanId;
    }

    public void setScanId(UUID scanId) {
        this.scanId = scanId;
    }

    public ScanStatusEnum getStatus() {
        return status;
    }

    public void setStatus(ScanStatusEnum status) {
        this.status = status;
    }
}
