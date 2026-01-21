package pccth.code.review.Backend.Messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import pccth.code.review.Backend.DTO.ScanWsEvent;

@Component
public class ScanStatusPublisher {

    private final RedisTemplate<String, String> redisTemplate;
    private final ChannelTopic topic;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ScanStatusPublisher(RedisTemplate<String, String> redisTemplate,
                               ChannelTopic scanStatusTopic) {
        this.redisTemplate = redisTemplate;
        this.topic = scanStatusTopic;
    }

    public void publish(ScanWsEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(topic.getTopic(), json);
            System.out.println("REDIS PUBLISH " + json);
        } catch (Exception e) {
            throw new RuntimeException("Failed to publish scan status", e);
        }
    }
}
