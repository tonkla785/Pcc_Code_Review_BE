package pccth.code.review.Backend.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pccth.code.review.Backend.DTO.Response.N8NResponseDTO;
import pccth.code.review.Backend.DTO.Response.N8NScanQueueResposneDTO;
import pccth.code.review.Backend.Service.WebhookScanService;

import java.util.UUID;

@RestController
@RequestMapping("/webhooks")
public class WebhookController {

    private final WebhookScanService webhookScanService;

    public WebhookController(WebhookScanService webhookScanService) {
        this.webhookScanService = webhookScanService;
    }

    @PostMapping("/scan")
    public ResponseEntity<N8NScanQueueResposneDTO> triggerScan(
            @RequestParam UUID projectId,
            @RequestParam(defaultValue = "main") String branch
    ) {
        return ResponseEntity.ok(
                webhookScanService.triggerScan(projectId, branch)
        );
    }

    @PostMapping("/scan/result")
    public ResponseEntity<N8NResponseDTO> receiveScanResult(
            @RequestBody N8NResponseDTO result
    ) {
        return ResponseEntity.ok(result);
    }
}

