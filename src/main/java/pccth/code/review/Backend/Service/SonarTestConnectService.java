package pccth.code.review.Backend.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.util.Base64;
import java.util.Map;

@Service
public class SonarTestConnectService {

    private static final String INTERNAL_SONAR_URL = "http://localhost:9000";

    private static final String PUBLIC_SONAR_DOMAIN = "code.pccth.com";

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

        if (!userInput.startsWith("https://")) {
            throw new IllegalArgumentException(
                    "sonarHostUrl must start with https://"
            );
        }

        URI uri = URI.create(userInput);

        if (uri.getHost() == null) {
            throw new IllegalArgumentException("Invalid sonarHostUrl");
        }

        if (!uri.getHost().equalsIgnoreCase(PUBLIC_SONAR_DOMAIN)) {
            throw new IllegalArgumentException(
                    "Only https://code.pccth.com is allowed"
            );
        }

        return INTERNAL_SONAR_URL;
    }
}
