package pccth.code.review.Backend.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pccth.code.review.Backend.DTO.Response.N8NResponseDTO;
import pccth.code.review.Backend.DTO.Response.ScanResponseDTO;
import pccth.code.review.Backend.Service.ScanService;

@RestController
@RequestMapping("/webhooks")
public class WebhookController {
    @Autowired
    private ScanService scanService;
    @PostMapping("/scan/result")
    public ResponseEntity<N8NResponseDTO> receiveScanResult(
            @RequestBody N8NResponseDTO result
    ) {
        System.out.println(result); // Result from sonarqube
        ScanResponseDTO response = scanService.updateScan(result);
        // Add Logic Here
        return ResponseEntity.ok(result);
    }
    @PostMapping("/scan/test")
    public ResponseEntity<N8NResponseDTO> receiveScanResultTest(
            @RequestBody N8NResponseDTO result
    ) {
        System.out.println(result); // Result from sonarqube
        ScanResponseDTO response = scanService.updateScan(result);
        // Add Logic Here
        return ResponseEntity.ok(result);
    }
}

