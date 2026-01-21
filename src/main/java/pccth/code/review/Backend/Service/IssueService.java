
package pccth.code.review.Backend.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.dao.DataIntegrityViolationException;
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
    @PersistenceContext
    private EntityManager entityManager;

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

        for (N8NIssueResponseDTO dto : batch.getIssues()) {

            if (dto.getIssueKey() == null) continue;

            UUID issueId = issueRepository.upsertIssue(
                    dto.getIssueKey(),
                    dto.getType(),
                    dto.getSeverity(),
                    dto.getRuleKey(),
                    dto.getComponent(),
                    dto.getLine(),
                    dto.getMessage(),
                    dto.getStatus() != null ? dto.getStatus() : "OPEN",
                    dto.getCreatedAt().toInstant()
            );

            IssueEntity issueRef = entityManager.getReference(IssueEntity.class, issueId);

            if (!scanIssueRepository.existsByScan_IdAndIssue_Id(scanId, issueId)) {
                ScanIssueEntity link = new ScanIssueEntity();
                link.setScan(scanRef);
                link.setIssue(issueRef);
                scanIssueRepository.save(link);
            }

            upsertIssueDetail(issueRef, dto);
        }
    }

    private void upsertIssueDetail(IssueEntity issue, N8NIssueResponseDTO dto) {

        boolean hasDetail =
                dto.getDescription() != null ||
                        dto.getVulnerableCode() != null ||
                        dto.getRecommendedFix() != null;

        if (!hasDetail) return;

        IssueDetailEntity detail = issueDetailRepository
                .findById(issue.getId())
                .orElseGet(IssueDetailEntity::new);

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
