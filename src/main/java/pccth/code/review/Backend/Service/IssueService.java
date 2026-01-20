
package pccth.code.review.Backend.Service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pccth.code.review.Backend.DTO.Request.CommentRequestDTO;
import pccth.code.review.Backend.DTO.Response.CommentResponseDTO;
import pccth.code.review.Backend.Entity.CommentEntity;
import pccth.code.review.Backend.Entity.IssueEntity;
import pccth.code.review.Backend.Entity.ScanEntity;
import pccth.code.review.Backend.Entity.UserEntity;
import pccth.code.review.Backend.Repository.CommentRepository;
import pccth.code.review.Backend.Repository.IssueRepository;
import pccth.code.review.Backend.Repository.UserRepository;

import java.util.*;

@Service
public class IssueService {
    private final IssueRepository issueRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    public IssueService(IssueRepository issueRepository, CommentRepository commentRepository, UserRepository userRepository) {
        this.issueRepository = issueRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
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

    public String getIssueDetail(UUID id) {
        return "Get issue details: " + id;
    }

    public String assignDeveloper(UUID id) {
        return "Assign developer to issue: " + id;
    }

    public CommentResponseDTO addComment(UUID issueId, CommentRequestDTO request) {
        IssueEntity issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new RuntimeException("Issue not found"));

        UserEntity user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        CommentEntity comment = new CommentEntity();
        comment.setIssue(issue);
        comment.setUser(user);
        comment.setComment(request.getComment());
        comment.setCreatedAt(new Date());

        CommentEntity savedComment = commentRepository.save(comment);

        return mapToCommentResponseDTO(savedComment);
    }

    private CommentResponseDTO mapToCommentResponseDTO(CommentEntity comment) {
        CommentResponseDTO dto = new CommentResponseDTO();
        dto.setId(comment.getId());
        dto.setIssue(comment.getIssue().getId());
        dto.setUser(comment.getUser().getId());
        dto.setComment(comment.getComment());
        dto.setCreatedAt(comment.getCreatedAt());
        return dto;
    }

    public String updateStatus(UUID id) {
        return "Update status of issue: " + id;
    }
}
