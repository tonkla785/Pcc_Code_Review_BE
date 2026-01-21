
package pccth.code.review.Backend.Service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pccth.code.review.Backend.DTO.Request.CommentRequestDTO;
import pccth.code.review.Backend.DTO.Response.CommentResponseDTO;
import pccth.code.review.Backend.DTO.Response.N8NIssueBatchResponseDTO;
import pccth.code.review.Backend.DTO.Response.N8NIssueResponseDTO;
import pccth.code.review.Backend.Entity.*;
import pccth.code.review.Backend.Repository.*;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class IssueService {
    private final IssueRepository issueRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final IssueDetailRepository issueDetailRepository;
    private final ScanIssueRepository scanIssueRepository;

    public IssueService(IssueRepository issueRepository, CommentRepository commentRepository, UserRepository userRepository, IssueDetailRepository issueDetailRepository, ScanIssueRepository scanIssueRepository) {
        this.issueRepository = issueRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.issueDetailRepository = issueDetailRepository;
        this.scanIssueRepository = scanIssueRepository;
    }

    @Transactional
    public void upsertIssuesFromN8n(N8NIssueBatchResponseDTO batch) {

        if (batch == null || batch.getIssues() == null) return;

        UUID scanId = UUID.fromString(batch.getScanId());

        ScanEntity scanRef = new ScanEntity();
        scanRef.setId(scanId);

        // issueKey ที่เจอใน scan รอบนี้
        Set<String> latestKeys = batch.getIssues().stream()
                .map(N8NIssueResponseDTO::getIssueKey)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        for (N8NIssueResponseDTO dto : batch.getIssues()) {
            if (dto.getIssueKey() == null) continue;

            // upsert ISSUE (หาโดย issueKey เท่านั้น)
            IssueEntity issue = issueRepository
                    .findByIssueKey(dto.getIssueKey())
                    .orElseGet(IssueEntity::new);

            boolean isNew = issue.getId() == null;

            if (isNew) {
                issue.setIssueKey(dto.getIssueKey());
                issue.setCreatedAt(dto.getCreatedAt());
            }

            issue.setType(dto.getType());
            issue.setSeverity(dto.getSeverity());
            issue.setRuleKey(dto.getRuleKey());
            issue.setComponent(dto.getComponent());
            issue.setLine(dto.getLine());
            issue.setMessage(dto.getMessage());
            issue.setStatus(dto.getStatus() != null ? dto.getStatus() : "OPEN");

            IssueEntity savedIssue = issueRepository.save(issue);

            // bind ISSUE ↔ SCAN ผ่าน scan_issues
            if (!scanIssueRepository.existsByScan_IdAndIssue_Id(scanId, savedIssue.getId())) {
                ScanIssueEntity link = new ScanIssueEntity();
                link.setScan(scanRef);
                link.setIssue(savedIssue);
                scanIssueRepository.save(link);
            }

            // upsert ISSUE_DETAIL (1:1)
            upsertIssueDetail(savedIssue, dto);
        }

        // ปิด issue ที่ไม่เจอใน scan นี้ (เฉพาะ scan นี้)
        List<ScanIssueEntity> existingLinks =
                scanIssueRepository.findAllByScan_Id(scanId);

        for (ScanIssueEntity link : existingLinks) {
            IssueEntity issue = link.getIssue();
            if (issue.getIssueKey() != null &&
                    !latestKeys.contains(issue.getIssueKey()) &&
                    !"RESOLVED".equalsIgnoreCase(issue.getStatus())) {

                issue.setStatus("RESOLVED");
                issueRepository.save(issue);
            }
        }
    }


    private void upsertIssueDetail(IssueEntity issue, N8NIssueResponseDTO dto) {

        boolean hasAnyDetail =
                dto.getDescription() != null ||
                        dto.getVulnerableCode() != null ||
                        dto.getRecommendedFix() != null;

        if (!hasAnyDetail) return;

        IssueDetailEntity detail = issueDetailRepository
                .findById(issue.getId())
                .orElseGet(IssueDetailEntity::new);

        // สำคัญมาก: ผูก entity ไม่ใช่ set id เอง
        detail.setIssue(issue);

        detail.setDescription(dto.getDescription());
        detail.setVulnerableCode(dto.getVulnerableCode());
        detail.setRecommendedFix(dto.getRecommendedFix());

        issueDetailRepository.save(detail);
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
}
