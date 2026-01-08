package pccth.code.review.Backend.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pccth.code.review.Backend.DTO.Response.N8NResponseDTO;

@RestController
@RequestMapping("/webhooks")
public class WebhookController {
    @PostMapping("/scan/result")
    public ResponseEntity<N8NResponseDTO> receiveScanResult(
            @RequestBody N8NResponseDTO result
    ) {
        return ResponseEntity.ok(result);
    }
}

