package pccth.code.review.Backend.Service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import pccth.code.review.Backend.DTO.Response.NotificationResponseDTO;
import pccth.code.review.Backend.DTO.Response.UserWsEvent;
import pccth.code.review.Backend.DTO.Response.VerifieddResponseDTO;

import java.util.UUID;

@Service
public class WebSocketNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketNotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendNotificationToUser(UUID userId, NotificationResponseDTO notification) {
        messagingTemplate.convertAndSend(
                "/topic/notifications/" + userId.toString(),
                notification);
    }

    public void broadcastGlobalNotification(NotificationResponseDTO notification) {
        messagingTemplate.convertAndSend("/topic/notifications/global", notification);
    }

    public void broadcastComment(String issueId, Object commentData) {
        messagingTemplate.convertAndSend("/topic/issue/" + issueId + "/comments", commentData);
    }

    public void sendUserVerifyStatus(UUID userId, String status) {
        messagingTemplate.convertAndSend(
                "/topic/user/" + userId + "/verify-status",
                new UserWsEvent(userId, status)
        );
    }
}
