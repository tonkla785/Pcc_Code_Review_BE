package pccth.code.review.Backend.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pccth.code.review.Backend.DTO.Response.SecurityMetricsResponseDTO;
import pccth.code.review.Backend.Service.SecurityMetricsService;

import java.util.UUID;

@RestController
@RequestMapping("/api")
public class SecurityController {

    private final SecurityMetricsService metricsService;

    public SecurityController(SecurityMetricsService metricsService) {
        this.metricsService = metricsService;
    }

    @GetMapping("/security/metrics")
    public ResponseEntity<SecurityMetricsResponseDTO> metricsAll() {
        return ResponseEntity.ok(metricsService.getMetricsSecurityAll());
    }

    @GetMapping("/security/metrics/{projectId}")
    public ResponseEntity<SecurityMetricsResponseDTO> metricsByProject(@PathVariable UUID projectId) {
        return ResponseEntity.ok(metricsService.getMetricsSecurityByProject(projectId));
    }
}
