package pccth.code.review.Backend.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pccth.code.review.Backend.DTO.Request.N8NRequestDTO;
import pccth.code.review.Backend.DTO.Response.N8NIssueBatchResponseDTO;
import pccth.code.review.Backend.DTO.Response.N8NResponseDTO;
import pccth.code.review.Backend.Service.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/webhooks")
public class WebhookController {
    private final ProjectService projectService;
    private final ScanService scanService;
    private final GitCloneService gitCloneService;
    private final SonarScanService sonarScanService;
    private final IssueService issueService;

    public WebhookController(
            ProjectService projectService,
            ScanService scanService,
            GitCloneService gitCloneService,
            SonarScanService sonarScanService,
            IssueService issueService
    ) {
        this.projectService = projectService;
        this.scanService = scanService;
        this.sonarScanService = sonarScanService;
        this.gitCloneService = gitCloneService;
        this.issueService = issueService;
    }

    @PostMapping("/scan/result")
    public ResponseEntity<N8NResponseDTO> receiveScanResult(@RequestBody N8NResponseDTO result) {

        // ✅ log ให้เห็นสถานะจริง ไม่ใช่ success ทื่อๆ
        System.out.println(
                "[/scan/result] projectId=" + result.getProjectId()
                        + " scanId=" + result.getScanId()
                        + " status=" + result.getStatus()
        );

        // Add Logic Here
        projectService.updateScanAt(result.getProjectId());
        scanService.updateScan(result);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/scan/git-clone")
    public ResponseEntity<?> gitClone(@RequestBody N8NRequestDTO req) {
        System.out.println(
                "[/scan/git-clone] scanId=" + req.getScanId()
                        + " projectId=" + req.getProjectId()
                        + " branch=" + req.getBranch()
                        + " hasGitToken=" + (req.getGitToken() != null && !req.getGitToken().isBlank())
        );

        try {
            return ResponseEntity.ok(gitCloneService.execute(req));
        } catch (Exception e) {
            // ✅ ส่ง error ให้ n8n เห็นชัด
            Map<String, Object> body = new HashMap<>();
            body.put("error", true);
            body.put("step", "git-clone");
            body.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
        }
    }

    @PostMapping("/scan/sonar-scan")
    public ResponseEntity<?> sonarScan(@RequestBody N8NRequestDTO req) {
        System.out.println(
                "[/scan/sonar-scan] scanId=" + req.getScanId()
                        + " projectId=" + req.getProjectId()
                        + " projectKey=" + req.getSonarProjectKey()
        );

        try {
            return ResponseEntity.ok(sonarScanService.execute(req));
        } catch (Exception e) {
            Map<String, Object> body = new HashMap<>();
            body.put("error", true);
            body.put("step", "sonar-scan");
            body.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
        }
    }

    @PostMapping("/scan/issue-data")
    public ResponseEntity<N8NIssueBatchResponseDTO> receiveIssueResult(@RequestBody N8NIssueBatchResponseDTO result) {
        System.out.println(
                "[/scan/issue-data] projectId=" + result.getProjectId()
                        + " scanId=" + result.getScanId()
                        + " issues=" + (result.getIssues() != null ? result.getIssues().size() : 0)
        );

        issueService.upsertIssuesFromN8n(result);
        return ResponseEntity.ok(result);
    }
}
