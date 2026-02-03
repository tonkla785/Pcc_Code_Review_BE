package pccth.code.review.Backend.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pccth.code.review.Backend.DTO.Request.SonarQubeConfigRequestDTO;
import pccth.code.review.Backend.DTO.Response.SonarQubeConfigResponseDTO;
import pccth.code.review.Backend.Service.SonarQubeConfigService;

import java.util.UUID;

@RestController
@RequestMapping("/settings/sonarqube")
public class SonarQubeConfigController {

    private final SonarQubeConfigService sonarQubeConfigService;

    public SonarQubeConfigController(SonarQubeConfigService sonarQubeConfigService) {
        this.sonarQubeConfigService = sonarQubeConfigService;
    }

    @GetMapping
    public ResponseEntity<SonarQubeConfigResponseDTO> getConfig(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        SonarQubeConfigResponseDTO config = sonarQubeConfigService.getByUserId(userId);
        return ResponseEntity.ok(config);
    }

    @PutMapping
    public ResponseEntity<SonarQubeConfigResponseDTO> updateConfig(
            Authentication authentication,
            @RequestBody SonarQubeConfigRequestDTO request) {
        UUID userId = UUID.fromString(authentication.getName());
        SonarQubeConfigResponseDTO updated = sonarQubeConfigService.updateConfig(userId, request);
        return ResponseEntity.ok(updated);
    }
}
