package pccth.code.review.Backend.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pccth.code.review.Backend.DTO.Response.N8NResponseDTO;
<<<<<<< HEAD
import pccth.code.review.Backend.DTO.Response.ScanResponseDTO;
import pccth.code.review.Backend.Service.ProjectService;
import pccth.code.review.Backend.Service.ScanService;
=======
import pccth.code.review.Backend.Service.ProjectService;
>>>>>>> main

@RestController
@RequestMapping("/webhooks")
public class WebhookController {
    private final ProjectService projectService;
<<<<<<< HEAD
    private final ScanService scanService;

    public WebhookController(ProjectService projectService, ScanService scanService) {
        this.projectService = projectService;
        this.scanService = scanService;
=======

    public WebhookController(ProjectService projectService) {
        this.projectService = projectService;
>>>>>>> main
    }

    @PostMapping("/scan/result")
    public ResponseEntity<N8NResponseDTO> receiveScanResult(
            @RequestBody N8NResponseDTO result) {

        System.out.println(result); // Result from sonarqube
        // Add Logic Here
<<<<<<< HEAD
        projectService.updateScanAt(result.getProjectId());
        scanService.updateScan(result);
=======
        projectService.updateRepository(result.getProjectId());
>>>>>>> main
        return ResponseEntity.ok(result);
    }
}
