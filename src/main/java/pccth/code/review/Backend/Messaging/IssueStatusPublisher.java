package pccth.code.review.Backend.Messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class IssueStatusPublisher {

    private final RedisTemplate<String, String> redisTemplate;
    private final ChannelTopic topic;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public IssueStatusPublisher(RedisTemplate<String, String> redisTemplate,
                                ChannelTopic issueStatusTopic) {
        this.redisTemplate = redisTemplate;
        this.topic = issueStatusTopic;
    }

    public void publish(IssueBroadcastPublisher.IssueChangeEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(topic.getTopic(), json);
        } catch (Exception e) {
            throw new RuntimeException("Failed to publish issue status", e);
        }
    }
}
