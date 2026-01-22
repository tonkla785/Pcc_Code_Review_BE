package pccth.code.review.Backend.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pccth.code.review.Backend.DTO.Request.N8NRequestDTO;
import pccth.code.review.Backend.DTO.Response.N8NIssueBatchResponseDTO;
import pccth.code.review.Backend.DTO.Response.N8NResponseDTO;
import pccth.code.review.Backend.DTO.ScanWsEvent;
import pccth.code.review.Backend.Messaging.ScanStatusPublisher;
import pccth.code.review.Backend.Service.*;

@RestController
@RequestMapping("/webhooks")
public class WebhookController {
    private final ProjectService projectService;
    private final ScanService scanService;
    private final GitCloneService gitCloneService;
    private final SonarScanService sonarScanService;
    private final IssueService issueService;
    private final ScanStatusPublisher scanStatusPublisher;

    public WebhookController(ProjectService projectService, ScanService scanService, GitCloneService gitCloneService, SonarScanService sonarScanService,IssueService issueService, ScanStatusPublisher scanStatusPublisher) {
        this.projectService = projectService;
        this.scanService = scanService;
        this.sonarScanService = sonarScanService;
        this.gitCloneService = gitCloneService;
        this.issueService = issueService;
        this.scanStatusPublisher = scanStatusPublisher;
    }

    @PostMapping("/scan/result")
    public ResponseEntity<N8NResponseDTO> receiveScanResult(@RequestBody N8NResponseDTO result) {

        System.out.println(result); // Result from sonarqube
        // Add Logic Here
        projectService.updateScanAt(result.getProjectId());
        scanService.updateScan(result);

        scanStatusPublisher.publish(
                new ScanWsEvent(result.getProjectId(), result.getStatus())
        );

        return ResponseEntity.ok(result);
    }

    @PostMapping("/scan/git-clone")
    public ResponseEntity<?> gitClone(@RequestBody N8NRequestDTO req) {
        return ResponseEntity.ok(gitCloneService.execute(req));
    }

    @PostMapping("/scan/sonar-scan")
    public ResponseEntity<?> sonarScan(@RequestBody N8NRequestDTO req) {
        return ResponseEntity.ok(sonarScanService.execute(req));
    }

    @PostMapping("/scan/issue-data")
    public ResponseEntity<N8NIssueBatchResponseDTO> receiveIssueResult(@RequestBody N8NIssueBatchResponseDTO result) {

        System.out.println(result); // Result from sonarqube
        result.getIssues().forEach(issue ->
                System.out.println(issue)
        );
        // Add Logic Here
        issueService.upsertIssuesFromN8n(result);
        return ResponseEntity.ok(result);
    }
}
