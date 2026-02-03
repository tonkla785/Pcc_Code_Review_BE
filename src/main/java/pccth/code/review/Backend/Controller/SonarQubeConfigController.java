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

    @GetMapping("/{userId}")
    public ResponseEntity<SonarQubeConfigResponseDTO> getConfig(@PathVariable String userId) {
        UUID userUUID = UUID.fromString(userId);
        SonarQubeConfigResponseDTO config = sonarQubeConfigService.getByUserId(userUUID);
        return ResponseEntity.ok(config);
    }

    @PutMapping
    public ResponseEntity<SonarQubeConfigResponseDTO> updateConfig(
            @RequestBody SonarQubeConfigRequestDTO request) {
        UUID userId = UUID.fromString(request.getUserId());
        SonarQubeConfigResponseDTO updated = sonarQubeConfigService.updateConfig(userId, request);
        return ResponseEntity.ok(updated);
    }
}
