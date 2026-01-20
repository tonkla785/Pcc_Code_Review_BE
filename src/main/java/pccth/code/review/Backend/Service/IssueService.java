
package pccth.code.review.Backend.Service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pccth.code.review.Backend.DTO.Request.CommentRequestDTO;
import pccth.code.review.Backend.DTO.Response.CommentResponseDTO;
import pccth.code.review.Backend.DTO.Response.N8NIssueBatchResponseDTO;
import pccth.code.review.Backend.DTO.Response.N8NIssueResponseDTO;
import pccth.code.review.Backend.Entity.*;
import pccth.code.review.Backend.Repository.CommentRepository;
import pccth.code.review.Backend.Repository.IssueDetailRepository;
import pccth.code.review.Backend.Repository.IssueRepository;
import pccth.code.review.Backend.Repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class IssueService {
    private final IssueRepository issueRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final IssueDetailRepository issueDetailRepository;
    public IssueService(IssueRepository issueRepository, CommentRepository commentRepository, UserRepository userRepository, IssueDetailRepository issueDetailRepository) {
        this.issueRepository = issueRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.issueDetailRepository = issueDetailRepository;
    }
    @Transactional
    public void upsertIssuesFromN8n(N8NIssueBatchResponseDTO batch) {
        if (batch == null || batch.getIssues() == null) return;

        UUID scanId = UUID.fromString(batch.getScanId());

        // set ล่าสุดที่เพิ่งได้มา (issueKey ทั้งหมดในรอบนี้)
        Set<String> latestKeys = batch.getIssues().stream()
                .map(N8NIssueResponseDTO::getIssueKey)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        for (N8NIssueResponseDTO dto : batch.getIssues()) {
            if (dto.getIssueKey() == null) continue;

            IssueEntity issue = issueRepository.findByScan_IdAndIssueKey(scanId, dto.getIssueKey())
                    .orElseGet(IssueEntity::new);

            // ถ้าเป็น insert ใหม่: ต้อง set scan ด้วย (ใช้ reference กัน query scan จริง)
            // NOTE: ถ้า issue เดิมอยู่คนละ scan แล้วมึงอยากให้มัน "ย้าย scan" ก็ต้อง decide เอง
            if (issue.getId() == null) {
                ScanEntity scanRef = new ScanEntity();
                scanRef.setId(scanId);
                issue.setScan(scanRef);
            }

            // update fields
            issue.setIssueKey(dto.getIssueKey());
            issue.setType(dto.getType());
            issue.setSeverity(dto.getSeverity());
            issue.setComponent(dto.getComponent());
            issue.setLine(dto.getLine());
            issue.setMessage(dto.getMessage());
            issue.setStatus(dto.getStatus() != null ? dto.getStatus() : "OPEN");
            issue.setCreatedAt(dto.getCreatedAt());
            issue.setRuleKey(dto.getRuleKey()); // หรือ issue.setRuleKey(dto.getRuleKey()) ถ้ามึง rename

            IssueEntity saved = issueRepository.save(issue);

            // upsert detail (ใช้ MapsId)
            upsertIssueDetail(saved, dto);
        }

        // ปิด issue ที่หายไปในรอบนี้ (เฉพาะ scan นี้)
        List<IssueEntity> existingForScan = issueRepository.findAllByScan_Id(scanId);
        for (IssueEntity dbIssue : existingForScan) {
            String key = dbIssue.getIssueKey();
            if (key != null && !latestKeys.contains(key)) {
                if (!"RESOLVED".equalsIgnoreCase(dbIssue.getStatus())) {
                    dbIssue.setStatus("RESOLVED");
                    issueRepository.save(dbIssue);
                }
            }
        }
    }

    private void upsertIssueDetail(IssueEntity issue, N8NIssueResponseDTO dto) {
        boolean hasAnyDetail =
                dto.getDescription() != null ||
                        dto.getVulnerableCode() != null ||
                        dto.getRecommendedFix() != null;

        if (!hasAnyDetail) return;

        UUID issueId = issue.getId();
        if (issueId == null) {
            // กันพลาด: ถ้า issue ยังไม่มี id แสดงว่ายังไม่ถูก save จริง
            throw new IllegalStateException("Issue id is null, cannot upsert issue_details");
        }

        IssueDetailEntity detail = issueDetailRepository.findById(issueId)
                .orElseGet(IssueDetailEntity::new);

        // ชุดสำคัญ: set id ก่อน save
        detail.setIssueId(issueId);

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

    public String updateStatus(UUID id) {
        return "Update status of issue: " + id;
    }
}
