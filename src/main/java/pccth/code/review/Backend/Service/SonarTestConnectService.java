package pccth.code.review.Backend.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.util.Base64;
import java.util.Map;

@Service
public class SonarTestConnectService {

    public boolean validateSonarToken(String userHost, String token) {

        String resolvedHost = resolveSonarHost(userHost);

        WebClient client = WebClient.builder()
                .baseUrl(resolvedHost)
                .defaultHeaders(h -> {
                    String auth = Base64.getEncoder()
                            .encodeToString((token + ":").getBytes());
                    h.set("Authorization", "Basic " + auth);
                })
                .build();

        Map<?, ?> res = client.get()
                .uri("/api/authentication/validate")
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        return Boolean.TRUE.equals(res.get("valid"));
    }

    private String resolveSonarHost(String userInput) {

        if (userInput == null || userInput.isBlank()) {
            throw new IllegalArgumentException("sonarHostUrl is required");
        }

        if (!userInput.startsWith("http://") && !userInput.startsWith("https://")) {
            throw new IllegalArgumentException(
                    "sonarHostUrl must start with http:// or https://"
            );
        }

        URI uri = URI.create(userInput);

        if (uri.getHost() == null) {
            throw new IllegalArgumentException("Invalid sonarHostUrl");
        }

        String host = userInput.endsWith("/")
                ? userInput.substring(0, userInput.length() - 1)
                : userInput;

        return host;
    }
}
