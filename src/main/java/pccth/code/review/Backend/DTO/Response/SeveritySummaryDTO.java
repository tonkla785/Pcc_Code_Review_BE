package pccth.code.review.Backend.DTO.Response;

import java.util.Map;

public class SeveritySummaryDTO {
    private Map<String, Long> severity;

    public SeveritySummaryDTO(Map<String, Long> severity) {
        this.severity = severity;
    }

    public Map<String, Long> getSeverity() {
        return severity;
    }

    public void setSeverity(Map<String, Long> severity) {
        this.severity = severity;
    }
}

