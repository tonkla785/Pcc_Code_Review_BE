package pccth.code.review.Backend.Controller;

import org.springframework.http.ResponseEntity;
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

    @GetMapping("/{userId}")
    public ResponseEntity<NotificationSettingsResponseDTO> getSettings(@PathVariable String userId) {
        UUID userUUID = UUID.fromString(userId);
        NotificationSettingsResponseDTO settings = notificationSettingsService.getByUserId(userUUID);
        return ResponseEntity.ok(settings);
    }

    @PutMapping
    public ResponseEntity<NotificationSettingsResponseDTO> updateSettings(
            @RequestBody NotificationSettingsRequestDTO request) {
        UUID userId = UUID.fromString(request.getUserId());
        NotificationSettingsResponseDTO updated = notificationSettingsService.updateSettings(userId, request);
        return ResponseEntity.ok(updated);
    }
}
