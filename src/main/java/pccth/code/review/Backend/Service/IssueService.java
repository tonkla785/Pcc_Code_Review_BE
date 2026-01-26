package pccth.code.review.Backend.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pccth.code.review.Backend.DTO.Request.IssueUpdateRequestDTO;
import pccth.code.review.Backend.DTO.Response.*;
import pccth.code.review.Backend.Entity.*;
import pccth.code.review.Backend.Repository.*;

import java.time.Instant;
import java.util.*;

@Service
public class IssueService {
    @PersistenceContext
    private EntityManager entityManager;

    private final IssueRepository issueRepository;
    private final IssueDetailRepository issueDetailRepository;
    private final ScanIssueRepository scanIssueRepository;
    private final CommentService commentService;
    private final ScanRepository scanRepository;
    private final ProjectRepository projectRepository;

    public IssueService(
            IssueRepository issueRepository,
            IssueDetailRepository issueDetailRepository,
            ScanIssueRepository scanIssueRepository,
            CommentService commentService,
            ScanRepository scanRepository,
            ProjectRepository projectRepository
    ) {
        this.issueRepository = issueRepository;
        this.issueDetailRepository = issueDetailRepository;
        this.scanIssueRepository = scanIssueRepository;
        this.commentService = commentService;
        this.scanRepository = scanRepository;
        this.projectRepository = projectRepository;
    }

    @Transactional
    public void upsertIssuesFromN8n(N8NIssueBatchResponseDTO batch) {

        if (batch == null || batch.getIssues() == null) return;

        UUID projectId = UUID.fromString(batch.getProjectId());
        UUID scanId = UUID.fromString(batch.getScanId());

        ScanEntity scan = scanRepository.findById(scanId)
                .orElseThrow(() -> new IllegalArgumentException("Scan not found"));

        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        for (N8NIssueResponseDTO dto : batch.getIssues()) {

            if (dto.getIssueKey() == null) continue;

            //ข้อมูลเยอะเลยใช้ native query
            UUID issueId = (UUID) entityManager
                    .createNativeQuery("""
                                INSERT INTO issues (
                                    issue_key,
                                    project_id,
                                    type,
                                    severity,
                                    rule_key,
                                    component,
                                    line,
                                    message,
                                    status,
                                    created_at
                                )
                                VALUES (
                                    :issueKey,
                                    :projectId,
                                    :type,
                                    :severity,
                                    :ruleKey,
                                    :component,
                                    :line,
                                    :message,
                                    :status,
                                    :createdAt
                                )
                                ON CONFLICT (issue_key)
                                DO UPDATE SET
                                    type = EXCLUDED.type,
                                    severity = EXCLUDED.severity,
                                    rule_key = EXCLUDED.rule_key,
                                    component = EXCLUDED.component,
                                    line = EXCLUDED.line,
                                    message = EXCLUDED.message,
                                    status = EXCLUDED.status
                                RETURNING id
                            """)
                    .setParameter("issueKey", dto.getIssueKey())
                    .setParameter("projectId", project.getId())
                    .setParameter("type", dto.getType())
                    .setParameter("severity", dto.getSeverity())
                    .setParameter("ruleKey", dto.getRuleKey())
                    .setParameter("component", dto.getComponent())
                    .setParameter("line", dto.getLine())
                    .setParameter("message", dto.getMessage())
                    .setParameter("status", dto.getStatus() != null ? dto.getStatus() : "OPEN")
                    .setParameter(
                            "createdAt",
                            dto.getCreatedAt() != null
                                    ? dto.getCreatedAt().toInstant()
                                    : Instant.now()
                    )
                    .getSingleResult();

            IssueEntity issueRef = entityManager.getReference(IssueEntity.class, issueId);

            if (!scanIssueRepository.existsByScan_IdAndIssue_Id(scanId, issueId)) {
                ScanIssueEntity link = new ScanIssueEntity();
                link.setScan(scan);
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
    public List<IssuesResponseDTO> getAllIssues() {

        List<IssueEntity> issues = issueRepository.findAll();
        List<IssuesResponseDTO> result = new ArrayList<>();

        for (IssueEntity issue : issues) {
            result.add(mapToIssuesResponseDTO(issue));
        }

        return result;
    }

    //get issue details by id issue
    public IssueDetailResponseDTO findIssueDetailsById(UUID id){
        IssueDetailEntity issueDetail = issueDetailRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Issue details not found"));

        IssueDetailResponseDTO dto = new IssueDetailResponseDTO();

        dto.setDescription(issueDetail.getDescription());
        dto.setVulnerableCode(issueDetail.getVulnerableCode());
        dto.setRecommendedFix(issueDetail.getRecommendedFix());

        return dto;
    }

    public IssuesResponseDTO findIssueById(UUID id) {

        IssueEntity issue = issueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Issue not found"));


        IssuesResponseDTO dto = new IssuesResponseDTO();

        dto.setId(issue.getId());
        dto.setProjcetId(issue.getProject().getId());
        dto.setIssueKey(issue.getIssueKey());
        dto.setType(issue.getType());
        dto.setSeverity(issue.getSeverity());
        dto.setRuleKey(issue.getRuleKey());
        dto.setLine(issue.getLine());
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
    public IssuesResponseDTO updateIssue(IssueUpdateRequestDTO req) {

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

        IssuesResponseDTO dto = new IssuesResponseDTO();
        dto.setId(saved.getId());
        dto.setProjcetId(saved.getProject().getId());
        dto.setIssueKey(saved.getIssueKey());
        dto.setType(saved.getType());
        dto.setRuleKey(saved.getRuleKey());
        dto.setLine(saved.getLine());
        dto.setSeverity(saved.getSeverity());
        dto.setComponent(saved.getComponent());
        dto.setMessage(saved.getMessage());
        dto.setStatus(saved.getStatus());
        dto.setCreatedAt(saved.getCreatedAt());
        if (saved.getAssignedTo() != null) dto.setAssignedTo(saved.getAssignedTo().getId());
        if (saved.getScanIssues() != null && !saved.getScanIssues().isEmpty()) {
            dto.setScanId(saved.getScanIssues().get(0).getScan().getId());
        }
        return dto;
    }

    @Transactional
    public List<IssuesResponseDTO> getIssuesByType() {
        return issueRepository
                .findByTypeIn(List.of("SECURITY_HOTSPOT", "VULNERABILITY"))
                .stream()
                .map(this::mapToIssuesResponseDTO)
                .toList();
    }

    private IssuesResponseDTO mapToIssuesResponseDTO(IssueEntity issue) {

        IssuesResponseDTO dto = new IssuesResponseDTO();

        dto.setId(issue.getId());
        dto.setProjcetId(issue.getProject().getId());
        dto.setIssueKey(issue.getIssueKey());
        dto.setType(issue.getType());
        dto.setSeverity(issue.getSeverity());
        dto.setRuleKey(issue.getRuleKey());
        dto.setLine(issue.getLine());
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
}