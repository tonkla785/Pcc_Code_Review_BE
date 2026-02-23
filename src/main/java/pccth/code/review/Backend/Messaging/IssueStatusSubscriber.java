package pccth.code.review.Backend.Messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class IssueStatusSubscriber implements MessageListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public IssueStatusSubscriber(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String payload = new String(message.getBody());
            IssueBroadcastPublisher.IssueChangeEvent event =
                    objectMapper.readValue(payload, IssueBroadcastPublisher.IssueChangeEvent.class);

            // Forward to WebSocket for all connected clients
            messagingTemplate.convertAndSend("/topic/issues", event);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
