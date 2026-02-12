package pccth.code.review.Backend.Messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import pccth.code.review.Backend.DTO.ScanWsEvent;

@Component
public class ScanStatusSubscriber implements MessageListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ScanStatusSubscriber(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String payload = new String(message.getBody());
            ScanWsEvent event = objectMapper.readValue(payload, ScanWsEvent.class);

            // ส่ง WebSocket ให้ UI ทุก backend instance จะทำอันนี้
            messagingTemplate.convertAndSend("/topic/scan-status", event);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
