package pccth.code.review.Backend.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pccth.code.review.Backend.DTO.Response.N8NResponseDTO;
import pccth.code.review.Backend.Service.IssueService;

@RestController
@RequestMapping("/webhooks")
public class WebhookController {

    private final IssueService issueService;

    public WebhookController(IssueService issueService) {
        this.issueService = issueService;
    }

    @PostMapping("/scan/result")
    public ResponseEntity<N8NResponseDTO> receiveScanResult(
            @RequestBody N8NResponseDTO result
    ) {

        System.out.println(result); // Result from sonarqube
        
        // Send data to IssueService for processing
        issueService.processScanResult(result);

        return ResponseEntity.ok(result);
    }
}
