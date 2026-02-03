package pccth.code.review.Backend.Service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import pccth.code.review.Backend.DTO.Response.NotificationResponseDTO;

import java.util.UUID;

@Service
public class WebSocketNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketNotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * ส่ง notification ไปยัง user เฉพาะคน
     */
    public void sendNotificationToUser(UUID userId, NotificationResponseDTO notification) {
        messagingTemplate.convertAndSend(
                "/topic/notifications/" + userId.toString(),
                notification);
    }

    /**
     * ส่ง notification ไปยังทุกคน (broadcast)
     */
    public void broadcastNotification(NotificationResponseDTO notification) {
        messagingTemplate.convertAndSend("/topic/notifications/all", notification);
    }

    /**
     * ส่ง scan status update ไปยังทุกคน
     */
    public void sendScanUpdate(Object scanUpdate) {
        messagingTemplate.convertAndSend("/topic/scans", scanUpdate);
    }

    /**
     * ส่ง issue update ไปยังทุกคน
     */
    public void sendIssueUpdate(Object issueUpdate) {
        messagingTemplate.convertAndSend("/topic/issues", issueUpdate);
    }
}
