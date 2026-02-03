package pccth.code.review.Backend.Controller;

import org.springframework.http.ResponseEntity;
<<<<<<< HEAD
=======
import org.springframework.security.core.Authentication;
>>>>>>> main
import org.springframework.web.bind.annotation.*;
import pccth.code.review.Backend.DTO.Request.NotificationRequestDTO;
import pccth.code.review.Backend.DTO.Response.NotificationResponseDTO;
import pccth.code.review.Backend.Service.NotificationService;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

<<<<<<< HEAD
    @GetMapping("/{userId}")
    public ResponseEntity<List<NotificationResponseDTO>> getAllNotifications(@PathVariable String userId) {
        UUID userUUID = UUID.fromString(userId);
        List<NotificationResponseDTO> notifications = notificationService.getAllByUserId(userUUID);
=======
    @GetMapping
    public ResponseEntity<List<NotificationResponseDTO>> getAllNotifications(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        List<NotificationResponseDTO> notifications = notificationService.getAllByUserId(userId);
>>>>>>> main
        return ResponseEntity.ok(notifications);
    }

    @PostMapping
    public ResponseEntity<NotificationResponseDTO> createNotification(
            @Valid @RequestBody NotificationRequestDTO request) {
        NotificationResponseDTO notification = notificationService.createNotification(request);
        return ResponseEntity.ok(notification);
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable UUID notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok().build();
    }

<<<<<<< HEAD
    @PatchMapping("/{userId}/read-all")
    public ResponseEntity<Void> markAllAsRead(@PathVariable String userId) {
        UUID userUUID = UUID.fromString(userId);
        notificationService.markAllAsRead(userUUID);
=======
    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        notificationService.markAllAsRead(userId);
>>>>>>> main
        return ResponseEntity.ok().build();
    }
}
