
package pccth.code.review.Backend.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pccth.code.review.Backend.DTO.Request.CommentRequestDTO;
import pccth.code.review.Backend.DTO.Request.IssueUpdateRequestDTO;
import pccth.code.review.Backend.DTO.Response.*;
import pccth.code.review.Backend.Entity.*;
import pccth.code.review.Backend.Repository.*;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class IssueService {
    @PersistenceContext
    private EntityManager entityManager;

    private final IssueRepository issueRepository;
    private final IssueDetailRepository issueDetailRepository;
    private final ScanIssueRepository scanIssueRepository;
    private final CommentService commentService;

    public IssueService(
            IssueRepository issueRepository,
            IssueDetailRepository issueDetailRepository,
            ScanIssueRepository scanIssueRepository,
            CommentService commentService
    ) {
        this.issueRepository = issueRepository;
        this.issueDetailRepository = issueDetailRepository;
        this.scanIssueRepository = scanIssueRepository;
        this.commentService = commentService;
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


    @Transactional(readOnly = true)
    public List<IssuesReponseDTO> getAllIssues() {

        List<IssueEntity> issues = issueRepository.findAll();
        List<IssuesReponseDTO> result = new ArrayList<>();

        for (IssueEntity issue : issues) {
            result.add(mapToIssuesResponseDTO(issue));
        }

        return result;
    }
    private IssuesReponseDTO mapToIssuesResponseDTO(IssueEntity issue) {

        IssuesReponseDTO dto = new IssuesReponseDTO();

        dto.setId(issue.getId());
        dto.setIssueKey(issue.getIssueKey());
        dto.setType(issue.getType());
        dto.setSeverity(issue.getSeverity());
        dto.setComponent(issue.getComponent());
        dto.setMessage(issue.getMessage());
        dto.setStatus(issue.getStatus());
        dto.setCreatedAt(issue.getCreatedAt());

        // assigned user
        if (issue.getAssignedTo() != null) {
            dto.setAssignedTo(issue.getAssignedTo().getId());
        }

        // scanId (issue อาจอยู่หลาย scan → เอาอันแรก)
        if (issue.getScanIssues() != null && !issue.getScanIssues().isEmpty()) {
            dto.setScanId(
                    issue.getScanIssues()
                            .get(0)
                            .getScan()
                            .getId()
            );
        }

        // comments
        List<CommentResponseDTO> comments = new ArrayList<>();
        if (issue.getCommentData() != null) {
            for (CommentEntity comment : issue.getCommentData()) {
                comments.add(commentService.mapToCommentResponseDTO(comment));
            }
        }
        dto.setCommentData(comments);

        return dto;
    }

    public IssuesReponseDTO findIssueById(UUID id) {

        IssueEntity issue = issueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Issue not found"));


        IssuesReponseDTO dto = new IssuesReponseDTO();

        dto.setId(issue.getId());
        dto.setIssueKey(issue.getIssueKey());
        dto.setType(issue.getType());
        dto.setSeverity(issue.getSeverity());
        dto.setComponent(issue.getComponent());
        dto.setMessage(issue.getMessage());
        dto.setStatus(issue.getStatus());
        dto.setCreatedAt(issue.getCreatedAt());

        // assigned user
        if (issue.getAssignedTo() != null) {
            dto.setAssignedTo(issue.getAssignedTo().getId());
        }

        // scanId (issue อาจอยู่หลาย scan → เอาอันแรก)
        if (issue.getScanIssues() != null && !issue.getScanIssues().isEmpty()) {
            dto.setScanId(
                    issue.getScanIssues()
                            .get(0)
                            .getScan()
                            .getId()
            );
        }

        // comments
        List<CommentResponseDTO> comments = new ArrayList<>();
        if (issue.getCommentData() != null) {
            for (CommentEntity comment : issue.getCommentData()) {
                comments.add(commentService.mapToCommentResponseDTO(comment));
            }
        }
        dto.setCommentData(comments);

        return dto;
    }

    @Transactional
    public IssuesReponseDTO updateIssue(IssueUpdateRequestDTO req) {

        IssueEntity issue = issueRepository.findById(req.getId())
                .orElseThrow(() -> new RuntimeException("Issue not found"));

        boolean hasAnyUpdate = false;

        // update status ถ้าส่งมา
        if (req.getStatus() != null) {
            issue.setStatus(req.getStatus());
            hasAnyUpdate = true;
        }

        //Note : หน้าบ้านถ้ามีการ UNASSIGNED ให้ส่งคำว่า UNASSIGNED มาจากหน้าบ้านด้วยนะ!!!!
        if (req.getAssignedTo() != null) {
            UserEntity userRef = entityManager.getReference(UserEntity.class, req.getAssignedTo());
            issue.setAssignedTo(userRef);
            hasAnyUpdate = true;
        }

        if (!hasAnyUpdate) {
            throw new RuntimeException("No fields to update (send status and/or assignedTo)");
        }

        IssueEntity saved = issueRepository.save(issue);

        IssuesReponseDTO dto = new IssuesReponseDTO();
        dto.setId(saved.getId());
        dto.setIssueKey(saved.getIssueKey());
        dto.setType(saved.getType());
        dto.setSeverity(saved.getSeverity());
        dto.setComponent(saved.getComponent());
        dto.setMessage(saved.getMessage());
        dto.setStatus(saved.getStatus());
        dto.setCreatedAt(saved.getCreatedAt());
        if (saved.getAssignedTo() != null) dto.setAssignedTo(saved.getAssignedTo().getId());
        if (saved.getScanIssues() != null && !saved.getScanIssues().isEmpty()) {
            dto.setScanId(saved.getScanIssues().get(0).getScan().getId());
        }
        // commentData ตามของเดิมมึงจะเติมต่อก็ได้
        return dto;
    }
}


