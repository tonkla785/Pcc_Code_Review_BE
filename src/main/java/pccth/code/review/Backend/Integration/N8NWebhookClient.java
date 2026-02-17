package pccth.code.review.Backend.Integration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import pccth.code.review.Backend.Config.WebhookConfig;

@Component
public class N8NWebhookClient {

    private final WebhookConfig webhookConfig;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${n8n.recommend.ai}")
    private String n8nUrlAi;

    public N8NWebhookClient(WebhookConfig webhookConfig) {
        this.webhookConfig = webhookConfig;
    }

    public <T> void postToN8N(T payload) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("WEBHOOK-TOKEN", webhookConfig.getWebhookToken());

        HttpEntity<T> entity = new HttpEntity<>(payload, headers);

        restTemplate.postForEntity(
                webhookConfig.getWorkerUrl(),
                entity,
                Void.class
        );
    }

    public <T> void postIssueToN8N(T payload){
        HttpHeaders headers = new HttpHeaders();
        headers.set("WEBHOOK-TOKEN", webhookConfig.getWebhookToken());

        HttpEntity<T> entity = new HttpEntity<>(payload, headers);

        restTemplate.postForEntity(
                n8nUrlAi,
                entity,
                Void.class
        );
    }
}
