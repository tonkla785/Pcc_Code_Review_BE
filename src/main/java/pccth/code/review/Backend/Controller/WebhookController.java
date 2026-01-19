package pccth.code.review.Backend.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pccth.code.review.Backend.DTO.Request.N8NRequestDTO;
<<<<<<< HEAD
import pccth.code.review.Backend.DTO.Response.N8NIssueResponseDTO;
=======
>>>>>>> main
import pccth.code.review.Backend.DTO.Response.N8NResponseDTO;
import pccth.code.review.Backend.DTO.Response.ScanResponseDTO;
import pccth.code.review.Backend.Service.GitCloneService;
import pccth.code.review.Backend.Service.ProjectService;
import pccth.code.review.Backend.Service.ScanService;
import pccth.code.review.Backend.Service.SonarScanService;

@RestController
@RequestMapping("/webhooks")
public class WebhookController {
    private final ProjectService projectService;
    private final ScanService scanService;
    private final GitCloneService gitCloneService;
    private final SonarScanService sonarScanService;

    public WebhookController(ProjectService projectService, ScanService scanService, GitCloneService gitCloneService, SonarScanService sonarScanService) {
        this.projectService = projectService;
        this.scanService = scanService;
        this.sonarScanService = sonarScanService;
        this.gitCloneService = gitCloneService;
    }

    @PostMapping("/scan/result")
    public ResponseEntity<N8NResponseDTO> receiveScanResult(@RequestBody N8NResponseDTO result) {

        System.out.println(result); // Result from sonarqube
        // Add Logic Here
        projectService.updateScanAt(result.getProjectId());
        scanService.updateScan(result);
        return ResponseEntity.ok(result);
    }

<<<<<<< HEAD
    @PostMapping("/scan/git-clone")
=======
    @PostMapping("/git-clone")
>>>>>>> main
    public ResponseEntity<?> gitClone(@RequestBody N8NRequestDTO req) throws Exception {
        return ResponseEntity.ok(gitCloneService.execute(req));
    }

<<<<<<< HEAD
    @PostMapping("/scan/sonar-scan")
    public ResponseEntity<?> sonarScan(@RequestBody N8NRequestDTO req) throws Exception {
        return ResponseEntity.ok(sonarScanService.execute(req));
    }

    @PostMapping("/scan/issue-data")
    public ResponseEntity<N8NIssueResponseDTO> issueData(@RequestBody N8NIssueResponseDTO result) throws Exception {
        System.out.println(result); // Result from sonarqube

        // Add Logic Here

        return ResponseEntity.ok(result);
    }
=======
    @PostMapping("/sonar-scan")
    public ResponseEntity<?> sonarScan(@RequestBody N8NRequestDTO req) throws Exception {
        return ResponseEntity.ok(sonarScanService.execute(req));
    }
>>>>>>> main
}
