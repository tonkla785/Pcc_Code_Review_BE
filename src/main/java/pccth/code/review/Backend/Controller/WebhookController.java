package pccth.code.review.Backend.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pccth.code.review.Backend.DTO.Response.N8NResponseDTO;
import pccth.code.review.Backend.DTO.Response.ScanResponseDTO;
import pccth.code.review.Backend.Service.ProjectService;
import pccth.code.review.Backend.Service.ScanService;

@RestController
@RequestMapping("/webhooks")
public class WebhookController {
    private final ProjectService projectService;
    private final ScanService scanService;

    public WebhookController(ProjectService projectService, ScanService scanService) {
        this.projectService = projectService;
        this.scanService = scanService;
    }

    @PostMapping("/scan/result")
    public ResponseEntity<N8NResponseDTO> receiveScanResult(
            @RequestBody N8NResponseDTO result) {

        System.out.println(result); // Result from sonarqube
        // Add Logic Here
        projectService.updateScanAt(result.getProjectId());
        scanService.updateScan(result);
        return ResponseEntity.ok(result);
    }
}
