package pccth.code.review.Backend.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pccth.code.review.Backend.DTO.Request.NotificationSettingsRequestDTO;
import pccth.code.review.Backend.DTO.Response.NotificationSettingsResponseDTO;
import pccth.code.review.Backend.Service.NotificationSettingsService;

import java.util.UUID;

@RestController
@RequestMapping("/settings/notification")
public class NotificationSettingsController {

    private final NotificationSettingsService notificationSettingsService;

    public NotificationSettingsController(NotificationSettingsService notificationSettingsService) {
        this.notificationSettingsService = notificationSettingsService;
    }

    @GetMapping
    public ResponseEntity<NotificationSettingsResponseDTO> getSettings(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        NotificationSettingsResponseDTO settings = notificationSettingsService.getByUserId(userId);
        return ResponseEntity.ok(settings);
    }

    @PutMapping
    public ResponseEntity<NotificationSettingsResponseDTO> updateSettings(
            Authentication authentication,
            @RequestBody NotificationSettingsRequestDTO request) {
        UUID userId = UUID.fromString(authentication.getName());
        NotificationSettingsResponseDTO updated = notificationSettingsService.updateSettings(userId, request);
        return ResponseEntity.ok(updated);
    }
}
