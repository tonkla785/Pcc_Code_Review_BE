package pccth.code.review.Backend.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pccth.code.review.Backend.DTO.Response.N8NResponseDTO;
<<<<<<< HEAD
import pccth.code.review.Backend.Service.IssueService;
=======
import pccth.code.review.Backend.DTO.Response.ScanResponseDTO;
import pccth.code.review.Backend.Service.ProjectService;
import pccth.code.review.Backend.Service.ScanService;
>>>>>>> main

@RestController
@RequestMapping("/webhooks")
public class WebhookController {
<<<<<<< HEAD

    private final IssueService issueService;

    public WebhookController(IssueService issueService) {
        this.issueService = issueService;
=======
    private final ProjectService projectService;
    private final ScanService scanService;

    public WebhookController(ProjectService projectService, ScanService scanService) {
        this.projectService = projectService;
        this.scanService = scanService;
>>>>>>> main
    }

    @PostMapping("/scan/result")
    public ResponseEntity<N8NResponseDTO> receiveScanResult(
            @RequestBody N8NResponseDTO result) {

        System.out.println(result); // Result from sonarqube
<<<<<<< HEAD
        
        // Send data to IssueService for processing
        issueService.processScanResult(result);

=======
        // Add Logic Here
        projectService.updateScanAt(result.getProjectId());
        scanService.updateScan(result);
>>>>>>> main
        return ResponseEntity.ok(result);
    }
}
