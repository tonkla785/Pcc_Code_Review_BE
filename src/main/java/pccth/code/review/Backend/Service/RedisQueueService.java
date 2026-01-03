package pccth.code.review.Backend.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import pccth.code.review.Backend.DTO.Request.N8NRequestDTO;

@Service
public class RedisQueueService {

    private static final String SCAN_QUEUE_KEY = "scan:queue";

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisQueueService(
            RedisTemplate<String, String> redisTemplate,
            ObjectMapper objectMapper
    ) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public void enqueueScan(N8NRequestDTO request) {
        try {
            String payload = objectMapper.writeValueAsString(request);
            redisTemplate.opsForList().rightPush(SCAN_QUEUE_KEY, payload);
        } catch (Exception e) {
            throw new RuntimeException("Failed to enqueue scan job", e);
        }
    }
}
