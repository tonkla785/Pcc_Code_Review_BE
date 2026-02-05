package pccth.code.review.Backend.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pccth.code.review.Backend.DTO.Request.EmailRequestDTO;
import pccth.code.review.Backend.DTO.Request.TriggerScanRequestDTO;
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
            @RequestBody TriggerScanRequestDTO requestDTO) {
        return ResponseEntity.accepted().body(
                webhookScanService.triggerScan(
                        projectId,
                        requestDTO.getBranch() != null ? requestDTO.getBranch() : "main",
                        requestDTO.getSonarToken(),
                        requestDTO.getGitToken(),        // << เพิ่ม
                        requestDTO.getAngularSettings(),
                        requestDTO.getSpringSettings()));
    }

    @PostMapping("/api/email")
    public ResponseEntity<Void> sendEmail(@RequestBody EmailRequestDTO dto) {
        emailService.send(dto);
        return ResponseEntity.accepted().build();
    }
}
