package pccth.code.review.Backend.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import pccth.code.review.Backend.DTO.Request.EmailRequestDTO;

@Service
public class EmailService {

    private final WebClient webClient;

    @Value("${n8n.email.url}")
    private String n8nUrl;

    @Value("${n8n.webhook.secret}")
    private String webhookSecret;

    public EmailService(WebClient.Builder builder) {
        this.webClient = builder.build();
    }

    public void send(EmailRequestDTO dto) {
        webClient.post()
                .uri(n8nUrl)
                .header("webhook-token", webhookSecret)
                .bodyValue(dto)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}
