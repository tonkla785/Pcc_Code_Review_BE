package pccth.code.review.Backend.DTO.Request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SonarRuleSyncRequestDTO {

    private Map<String, List<String>> owasp = new HashMap<>();
    private Map<String, List<String>> security = new HashMap<>();

    public Map<String, List<String>> getOwasp() {
        return owasp;
    }

    public void setOwasp(Map<String, List<String>> owasp) {
        this.owasp = owasp;
    }

    public Map<String, List<String>> getSecurity() {
        return security;
    }

    public void setSecurity(Map<String, List<String>> security) {
        this.security = security;
    }
}
