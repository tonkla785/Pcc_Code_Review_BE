package pccth.code.review.Backend.DTO.Response;

import java.util.List;
import java.util.UUID;

public class N8NIssueBatchResponseDTO {
    private String scanId;
    private String projectId;
    private String projectKey;
    private List<N8NIssueResponseDTO> issues;

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getScanId() {
        return scanId;
    }

    public void setScanId(String scanId) {
        this.scanId = scanId;
    }

    public String getProjectKey() {
        return projectKey;
    }

    public void setProjectKey(String projectKey) {
        this.projectKey = projectKey;
    }

    public List<N8NIssueResponseDTO> getIssues() {
        return issues;
    }

    public void setIssues(List<N8NIssueResponseDTO> issues) {
        this.issues = issues;
    }

//    @Override
//    public String toString() {
//        return """
//                N8NIssueBatchRequest {
//                  scanId     = %s
//                  projectKey = %s
//                  issueCount = %d
//                }
//                """.formatted(
//                scanId,
//                projectKey,
//                issues != null ? issues.size() : 0
//        );
//    }
}
