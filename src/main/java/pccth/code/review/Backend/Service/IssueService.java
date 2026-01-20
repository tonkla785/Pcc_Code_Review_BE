package pccth.code.review.Backend.Service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pccth.code.review.Backend.Entity.IssueEntity;
import pccth.code.review.Backend.Entity.ScanEntity;
import pccth.code.review.Backend.Repository.IssueRepository;

import java.util.*;

@Service
public class IssueService {
    private final IssueRepository issueRepository;

    public IssueService(IssueRepository issueRepository) {
        this.issueRepository = issueRepository;
    }

    public List<IssueEntity> listIssues() {
        return issueRepository.findAll();
    }

    @Transactional
    public List<IssueEntity> processFromMetrics(ScanEntity scan, Map<String, Object> metrics) {
        if (scan == null) throw new IllegalArgumentException("scan is null");
        if (metrics == null) metrics = Collections.emptyMap();


        List<IssueEntity> issues = new ArrayList<>();

        int bugs = toInt(metrics.get("bugs"));
        int vulns = toInt(metrics.get("vulnerabilities"));
        int hotspots = toInt(metrics.get("securityHotspots"));
        int smells = toInt(metrics.get("codeSmells"));

        if (bugs > 0) {
            issues.add(buildIssue(scan, "BUG", "CRITICAL", "SonarQube", "Bugs found: " + bugs));
        }
        if (vulns > 0) {
            issues.add(buildIssue(scan, "VULNERABILITY", "CRITICAL", "SonarQube", "Vulnerabilities found: " + vulns));
        }
        if (hotspots > 0) {
            issues.add(buildIssue(scan, "SECURITY_HOTSPOT", "MAJOR", "SonarQube", "Security hotspots: " + hotspots));
        }
        if (smells > 0) {
            issues.add(buildIssue(scan, "CODE_SMELL", "MINOR", "SonarQube", "Code smells: " + smells));
        }

        return issueRepository.saveAll(issues);
    }

    private IssueEntity buildIssue(ScanEntity scan, String type, String severity, String component, String message) {
        IssueEntity issue = new IssueEntity();
        issue.setScan(scan);
        issue.setIssueKey(null);
        issue.setType(type);
        issue.setSeverity(severity);
        issue.setComponent(component);
        issue.setMessage(message);
        issue.setStatus("OPEN");
        issue.setCreatedAt(new Date());
        issue.setAssignedTo(null);
        return issue;
    }

    private int toInt(Object v) {
        if (v == null) return 0;
        if (v instanceof Number n) return n.intValue();
        try { return Integer.parseInt(String.valueOf(v)); }
        catch (Exception e) { return 0; }
    }

    public String getIssueDetail(Long id) {
        return "Get issue details: " + id;
    }

    public String assignDeveloper(Long id) {
        return "Assign developer to issue: " + id;
    }

    public String addComment(Long id) {
        return "Add comment to issue: " + id;
    }

    public String updateStatus(Long id) {
        return "Update status of issue: " + id;
    }
}
