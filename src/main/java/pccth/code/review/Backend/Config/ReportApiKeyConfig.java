package pccth.code.review.Backend.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReportApiKeyConfig {

    @Value("${report.api-key:}")
    private String apiKey;

    public String getApiKey() {
        return apiKey;
    }
}
