package pccth.code.review.Backend.Config;

import org.springframework.beans.factory.annotation.Value;

public class WebhookConfig {
    @Value("${webhook.token}")
    private String webhookToken;

    public String getWebhookToken() {
        return webhookToken;
    }
}
