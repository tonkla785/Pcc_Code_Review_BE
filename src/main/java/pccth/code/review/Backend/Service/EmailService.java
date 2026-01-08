package pccth.code.review.Backend.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import pccth.code.review.Backend.DTO.Request.EmailRequestDTO;

@Service
public class EmailService {

    private final WebClient webClient;

    public EmailService(WebClient.Builder builder) {
        this.webClient = builder.build();
    }

    public void send(EmailRequestDTO dto) {
        webClient.post()
                .uri("http://localhost:5678/webhook/email-service")
                .bodyValue(dto)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}

