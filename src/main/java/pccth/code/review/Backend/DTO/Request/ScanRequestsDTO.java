package pccth.code.review.Backend.DTO.Request;

import pccth.code.review.Backend.DTO.Response.IssuesReponseDTO;
import pccth.code.review.Backend.Entity.ProjectEntity;
import pccth.code.review.Backend.EnumType.ScanStatusEnum;

import java.util.*;

public class ScanRequestsDTO {

    private UUID projectId;
    private ScanStatusEnum status;

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public ScanStatusEnum getStatus() {
        return status;
    }

    public void setStatus(ScanStatusEnum status) {
        this.status = status;
    }
}
