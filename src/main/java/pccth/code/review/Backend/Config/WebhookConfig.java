package pccth.code.review.Backend.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebhookConfig {
    @Value("${webhook.token}")
    private String webhookToken;

    public String getWebhookToken() {
        return webhookToken;
    }
}
