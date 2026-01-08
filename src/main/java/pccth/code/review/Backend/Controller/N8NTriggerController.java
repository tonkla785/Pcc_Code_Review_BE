package pccth.code.review.Backend.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pccth.code.review.Backend.DTO.Response.N8NScanQueueResposneDTO;
import pccth.code.review.Backend.Service.WebhookScanService;

import java.util.UUID;

@RestController
public class N8NTriggerController {

    private final WebhookScanService webhookScanService;

    public N8NTriggerController(WebhookScanService webhookScanService) {
        this.webhookScanService = webhookScanService;
    }

    @PostMapping("/{projectId}/scan")
    public ResponseEntity<N8NScanQueueResposneDTO> requestScan(
            @PathVariable UUID projectId,
            @RequestParam(defaultValue = "main") String branch
    ) {
        return ResponseEntity.accepted().body(
                webhookScanService.triggerScan(projectId, branch)
        );
    }
}
