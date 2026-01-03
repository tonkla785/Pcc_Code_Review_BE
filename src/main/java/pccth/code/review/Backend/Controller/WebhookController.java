package pccth.code.review.Backend.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pccth.code.review.Backend.DTO.N8NResultDTO;
import pccth.code.review.Backend.DTO.Response.N8NScanQueueResposneDTO;
import pccth.code.review.Backend.Service.WebhookScanService;

import java.util.UUID;

@RestController
@RequestMapping("/webhooks")
public class WebhookController {

    private final WebhookScanService scanService;

    public WebhookController(WebhookScanService scanService) {
        this.scanService = scanService;
    }

    @PostMapping("/scan")
    public ResponseEntity<N8NScanQueueResposneDTO> triggerScan(
            @RequestParam UUID projectId,
            @RequestParam(defaultValue = "main") String branch
    ) {
        return ResponseEntity.ok(
                scanService.triggerScan(projectId, branch)
        );
    }

    @PostMapping("/scan/result")
    public ResponseEntity<N8NResultDTO> receiveScanResult(
            @RequestBody N8NResultDTO result
    ) {
        return ResponseEntity.ok(result);
    }
}

