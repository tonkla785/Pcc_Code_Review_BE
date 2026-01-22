package pccth.code.review.Backend.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import pccth.code.review.Backend.Messaging.ScanStatusSubscriber;

@Configuration
public class RedisConfig {

    public static final String TOPIC_SCAN_STATUS = "scan-status";

    @Bean
    public RedisTemplate<String, String> redisTemplate(
            RedisConnectionFactory factory) {

        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }

    // 1. กำหนด Topic ที่จะใช้
    @Bean
    public ChannelTopic scanStatusTopic() {
        return new ChannelTopic(TOPIC_SCAN_STATUS);
    }

    // 2. สร้าง Container สำหรับฟัง Redis ผูกกับ Subscriber
    @Bean
    public RedisMessageListenerContainer redisContainer(
            RedisConnectionFactory factory,
            ScanStatusSubscriber subscriber,
            ChannelTopic scanStatusTopic
    ) {
        RedisMessageListenerContainer container =
                new RedisMessageListenerContainer();

        container.setConnectionFactory(factory);
        container.addMessageListener(subscriber, scanStatusTopic);

        return container;
    }
}
