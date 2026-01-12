package pccth.code.review.Backend.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pccth.code.review.Backend.DTO.Response.N8NResponseDTO;
import pccth.code.review.Backend.Service.ProjectService;

@RestController
@RequestMapping("/webhooks")
public class WebhookController {

    private final ProjectService projectService;

    public WebhookController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping("/scan/result")
    public ResponseEntity<N8NResponseDTO> receiveScanResult(
            @RequestBody N8NResponseDTO result) {

        System.out.println(result); // Result from sonarqube
        // Add Logic Here
        projectService.updateRepository(result.getProjectId());

        return ResponseEntity.ok(result);
    }
}
