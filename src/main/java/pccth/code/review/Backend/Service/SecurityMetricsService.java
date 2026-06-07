package pccth.code.review.Backend.Service;

import org.springframework.stereotype.Service;
import pccth.code.review.Backend.DTO.Response.SecurityMetricsResponseDTO;
import pccth.code.review.Backend.DTO.Response.SecurityMetricsResponseDTO.CountItem;
import pccth.code.review.Backend.Entity.IssueEntity;
import pccth.code.review.Backend.Entity.SonarRuleSecurityEntity;
import pccth.code.review.Backend.Repository.IssueRepository;
import pccth.code.review.Backend.Repository.SonarRuleSecurityRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class SecurityMetricsService {

    private static final List<String> SECURITY_TYPES = List.of("VULNERABILITY", "SECURITY_HOTSPOT");

    private static final Map<String, Integer> DEDUCT = Map.of(
            "BLOCKER", 20, "CRITICAL", 10, "MAJOR", 5, "MINOR", 1);
    private static final Map<String, String> SEV_LABEL = Map.of(
            "BLOCKER", "Critical", "CRITICAL", "High", "MAJOR", "Medium", "MINOR", "Low");

    private static final List<Map.Entry<String, String>> OWASP_TOP10 = List.of(
            Map.entry("a1", "A01 Broken Access"),
            Map.entry("a2", "A02 Crypto Failures"),
            Map.entry("a3", "A03 Injection"),
            Map.entry("a4", "A04 Insecure Design"),
            Map.entry("a5", "A05 Security Config"),
            Map.entry("a6", "A06 Vulnerable Comp"),
            Map.entry("a7", "A07 Auth Failures"),
            Map.entry("a8", "A08 Data Integrity"),
            Map.entry("a9", "A09 Logging Fails"),
            Map.entry("a10", "A10 SSRF"));

    private final IssueRepository issueRepository;
    private final SonarRuleSecurityRepository ruleRepo;

    public SecurityMetricsService(IssueRepository issueRepository,
                                  SonarRuleSecurityRepository ruleRepo) {
        this.issueRepository = issueRepository;
        this.ruleRepo = ruleRepo;
    }

    public SecurityMetricsResponseDTO getMetricsSecurityAll() {
        return analyze(openSecurityIssues());
    }

    public SecurityMetricsResponseDTO getMetricsSecurityByProject(UUID projectId) {
        List<IssueEntity> issues = openSecurityIssues().stream()
                .filter(i -> i.getProject() != null && projectId.equals(i.getProject().getId()))
                .toList();
        return analyze(issues);
    }

    private List<IssueEntity> openSecurityIssues() {
        return issueRepository.findByTypeIn(SECURITY_TYPES).stream()
                .filter(i -> {
                    String s = i.getStatus() == null ? "" : i.getStatus().toUpperCase();
                    return !s.equals("RESOLVED") && !s.equals("CLOSED");
                })
                .toList();
    }

    private SecurityMetricsResponseDTO analyze(List<IssueEntity> issues) {
        Map<String, SonarRuleSecurityEntity> ruleMap = new HashMap<>();
        for (SonarRuleSecurityEntity e : ruleRepo.findAll()) {
            ruleMap.put(e.getRuleKey(), e);
        }

        int totalDeduction = 0;
        Set<String> severitySet = new HashSet<>();

        Map<String, Integer> sevCount = new LinkedHashMap<>();
        sevCount.put("Critical", 0);
        sevCount.put("High", 0);
        sevCount.put("Medium", 0);
        sevCount.put("Low", 0);

        Map<String, Integer> owaspCount = new LinkedHashMap<>();
        Map<String, Integer> hotCount = new LinkedHashMap<>();

        for (IssueEntity issue : issues) {
            String sev = issue.getSeverity() == null ? "" : issue.getSeverity().toUpperCase();
            if (DEDUCT.containsKey(sev)) {
                totalDeduction += DEDUCT.get(sev);
                sevCount.merge(SEV_LABEL.get(sev), 1, Integer::sum);
            }
            if (!sev.isEmpty()) {
                severitySet.add(sev);
            }

            SonarRuleSecurityEntity rule = ruleMap.get(issue.getRuleKey());
            if (rule != null) {
                if (rule.getOwaspCategories() != null) {
                    for (String o : rule.getOwaspCategories()) {
                        owaspCount.merge(o, 1, Integer::sum);
                    }
                }
                if (rule.getSecurityCategories() != null) {
                    for (String s : rule.getSecurityCategories()) {
                        hotCount.merge(s, 1, Integer::sum);
                    }
                }
            }
        }

        SecurityMetricsResponseDTO dto = new SecurityMetricsResponseDTO();
        dto.setScore(Math.max(0, 100 - totalDeduction));
        dto.setRiskLevel(riskLevel(severitySet));

        List<CountItem> vulns = new ArrayList<>();
        for (Map.Entry<String, Integer> e : sevCount.entrySet()) {
            vulns.add(new CountItem(e.getKey(), e.getValue(), null));
        }
        dto.setVulnerabilities(vulns);

        List<CountItem> hot = new ArrayList<>();
        hotCount.entrySet().stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .forEach(e -> hot.add(new CountItem(e.getKey(), e.getValue(), null)));
        dto.setHotIssues(hot);

        List<CountItem> owasp = new ArrayList<>();
        for (Map.Entry<String, String> cat : OWASP_TOP10) {
            int c = owaspCount.getOrDefault(cat.getKey(), 0);
            owasp.add(new CountItem(cat.getValue(), c, owaspStatus(c)));
        }
        dto.setOwaspCoverage(owasp);

        return dto;
    }

    private String riskLevel(Set<String> sev) {
        if (sev.contains("BLOCKER")) return "CRITICAL";
        if (sev.contains("CRITICAL")) return "HIGH";
        if (sev.contains("MAJOR")) return "MEDIUM";
        if (sev.contains("MINOR")) return "LOW";
        return "SAFE";
    }

    private String owaspStatus(int count) {
        if (count == 0) return "pass";
        if (count <= 2) return "warning";
        return "fail";
    }
}
