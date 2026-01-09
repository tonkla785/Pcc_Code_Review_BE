package pccth.code.review.Backend.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pccth.code.review.Backend.DTO.Request.EmailRequestDTO;
import pccth.code.review.Backend.DTO.Response.N8NScanQueueResposneDTO;
import pccth.code.review.Backend.Service.EmailService;
import pccth.code.review.Backend.Service.WebhookScanService;

import java.util.UUID;

@RestController
public class N8NTriggerController {

    private final WebhookScanService webhookScanService;
    private final EmailService emailService;

    public N8NTriggerController(WebhookScanService webhookScanService, EmailService emailService) {
        this.webhookScanService = webhookScanService;
        this.emailService = emailService;
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

    @PostMapping("/api/email")
    public ResponseEntity<Void> sendEmail(@RequestBody EmailRequestDTO dto) {
        emailService.send(dto);
        return ResponseEntity.accepted().build();
    }
}