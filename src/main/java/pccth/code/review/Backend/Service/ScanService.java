package pccth.code.review.Backend.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import pccth.code.review.Backend.DTO.Request.ScanRequestsDTO;
import pccth.code.review.Backend.DTO.Response.*;
import pccth.code.review.Backend.Entity.IssueEntity;
import pccth.code.review.Backend.Entity.ProjectEntity;
import pccth.code.review.Backend.Entity.ScanEntity;
import pccth.code.review.Backend.Repository.ProjectRepository;
import pccth.code.review.Backend.Repository.ScanRepository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScanService {

    @Autowired
    private ScanRepository scanRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private IssueService issueService;

    public List<ScanResponseDTO> getScansHistory(UUID projectId) {
        try {
            List<ScanEntity> scans = scanRepository.findByProjectId(projectId);
            List<ScanResponseDTO> responseDTOs = new ArrayList<>();
            for (ScanEntity e : scans) {
                ProjectEntity project = e.getProject();
                ProjectResponseDTO projectResponseDTO = new ProjectResponseDTO();
                projectResponseDTO.setId(project.getId());
                projectResponseDTO.setName(project.getName());
                projectResponseDTO.setRepositoryUrl(project.getRepositoryUrl());
                projectResponseDTO.setProjectType(project.getProjectType());
                projectResponseDTO.setSonarProjectKey(project.getSonarProjectKey());
                projectResponseDTO.setCreatedAt(project.getCreatedAt());
                projectResponseDTO.setUpdatedAt(project.getUpdatedAt());

                ScanResponseDTO scanResponseDTO = new ScanResponseDTO();
                scanResponseDTO.setId(e.getId());
                scanResponseDTO.setProject(projectResponseDTO);
                scanResponseDTO.setStatus(e.getStatus());
                scanResponseDTO.setStartedAt(e.getStartedAt());
                scanResponseDTO.setCompletedAt(e.getCompletedAt());
                scanResponseDTO.setQualityGate(e.getQualityGate());
                scanResponseDTO.setMetrics(e.getMetrics());
                scanResponseDTO.setLogFilePath(e.getLogFilePath());
                responseDTOs.add(scanResponseDTO);
            }
            return responseDTOs;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ScanResponseDTO getScansById(UUID scanId) {
        try {
            ScanEntity scans = scanRepository.findById(scanId)
                    .orElseThrow(() -> new RuntimeException("Scan not found"));

            ScanResponseDTO scanResponseDTO = new ScanResponseDTO();
            scanResponseDTO.setId(scans.getId());
            scanResponseDTO.setStatus(scans.getStatus());
            scanResponseDTO.setStartedAt(scans.getStartedAt());
            scanResponseDTO.setCompletedAt(scans.getCompletedAt());
            scanResponseDTO.setQualityGate(scans.getQualityGate());
            scanResponseDTO.setMetrics(scans.getMetrics());
            scanResponseDTO.setLogFilePath(scans.getLogFilePath());

            scanResponseDTO.setIssueData(
                    scans.getIssueData().stream().map(issue -> {
                        IssuesReponseDTO idto = new IssuesReponseDTO();
                        idto.setId(issue.getId());
                        idto.setScanId(issue.getScan().getId());
                        idto.setIssueKey(issue.getIssueKey());
                        idto.setType(issue.getType());
                        idto.setSeverity(issue.getSeverity());
                        idto.setComponent(issue.getComponent());
                        idto.setMessage(issue.getMessage());

                        idto.setAssignedTo(issue.getAssignedTo() != null ? issue.getAssignedTo().getId() : null);

                        idto.setStatus(issue.getStatus());
                        idto.setCreatedAt(issue.getCreatedAt());

                        idto.setCommentData(
                                issue.getCommentData().stream().map(comment -> {
                                    CommentResponseDTO commentResponseDTO = new CommentResponseDTO();
                                    commentResponseDTO.setId(comment.getId());
                                    commentResponseDTO.setComment(comment.getComment());
                                    commentResponseDTO.setCreatedAt(comment.getCreatedAt());
                                    commentResponseDTO.setIssue(comment.getIssue().getId());
                                    commentResponseDTO.setUser(comment.getUser().getId());
                                    return commentResponseDTO;
                                }).toList());
                        return idto;
                    }).toList());

            return scanResponseDTO;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<ScanResponseDTO> getScansAll() {
        try {
            List<ScanEntity> scans = scanRepository.findAll();
            List<ScanResponseDTO> responseDTOs = new ArrayList<>();
            for (ScanEntity e : scans) {
                ProjectEntity project = e.getProject();
                ProjectResponseDTO projectResponseDTO = new ProjectResponseDTO();
                projectResponseDTO.setId(project.getId());
                projectResponseDTO.setName(project.getName());
                projectResponseDTO.setRepositoryUrl(project.getRepositoryUrl());
                projectResponseDTO.setProjectType(project.getProjectType());
                projectResponseDTO.setSonarProjectKey(project.getSonarProjectKey());
                projectResponseDTO.setCreatedAt(project.getCreatedAt());
                projectResponseDTO.setUpdatedAt(project.getUpdatedAt());

                ScanResponseDTO scanResponseDTO = new ScanResponseDTO();
                scanResponseDTO.setId(e.getId());
                scanResponseDTO.setProject(projectResponseDTO);
                scanResponseDTO.setStatus(e.getStatus());
                scanResponseDTO.setStartedAt(e.getStartedAt());
                scanResponseDTO.setCompletedAt(e.getCompletedAt());
                scanResponseDTO.setQualityGate(e.getQualityGate());
                scanResponseDTO.setMetrics(e.getMetrics());
                scanResponseDTO.setLogFilePath(e.getLogFilePath());

                scanResponseDTO.setIssueData(
                        e.getIssueData().stream().map(issue -> {
                            IssuesReponseDTO idto = new IssuesReponseDTO();
                            idto.setId(issue.getId());
                            idto.setScanId(issue.getScan().getId());
                            idto.setIssueKey(issue.getIssueKey());
                            idto.setType(issue.getType());
                            idto.setSeverity(issue.getSeverity());
                            idto.setComponent(issue.getComponent());
                            idto.setMessage(issue.getMessage());

                            idto.setAssignedTo(issue.getAssignedTo() != null ? issue.getAssignedTo().getId() : null);

                            idto.setStatus(issue.getStatus());
                            idto.setCreatedAt(issue.getCreatedAt());
                            idto.setCommentData(
                                    issue.getCommentData().stream().map(comment -> {
                                        CommentResponseDTO commentResponseDTO = new CommentResponseDTO();
                                        commentResponseDTO.setId(comment.getId());
                                        commentResponseDTO.setComment(comment.getComment());
                                        commentResponseDTO.setCreatedAt(comment.getCreatedAt());
                                        commentResponseDTO.setIssue(comment.getIssue().getId());
                                        commentResponseDTO.setUser(comment.getUser().getId());
                                        return commentResponseDTO;
                                    }).toList());
                            return idto;
                        }).toList());

                responseDTOs.add(scanResponseDTO);
            }
            return responseDTOs;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ScanResponseDTO getScansLog(UUID scanId) {
        try {
            ScanEntity scans = scanRepository.findById(scanId)
                    .orElseThrow(() -> new RuntimeException("Scan not found"));
            ScanResponseDTO scanResponseDTO = new ScanResponseDTO();
            scanResponseDTO.setLogFilePath(scans.getLogFilePath());
            return scanResponseDTO;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ProjectResponseDTO getProject(UUID projectId) {
        try {
            ProjectEntity projectEntity = projectRepository.findById(projectId)
                    .orElseThrow(() -> new RuntimeException("Project not found"));

            ProjectResponseDTO projectResponseDTO = new ProjectResponseDTO();
            projectResponseDTO.setId(projectEntity.getId());
            projectResponseDTO.setName(projectEntity.getName());
            projectResponseDTO.setRepositoryUrl(projectEntity.getRepositoryUrl());
            projectResponseDTO.setProjectType(projectEntity.getProjectType());
            projectResponseDTO.setSonarProjectKey(projectEntity.getSonarProjectKey());
            projectResponseDTO.setCreatedAt(projectEntity.getCreatedAt());
            projectResponseDTO.setUpdatedAt(projectEntity.getUpdatedAt());

            List<ScanResponseDTO> scans = projectEntity.getScanData().stream()
                    .map(s -> {
                        ScanResponseDTO scanResponseDTOdto = new ScanResponseDTO();
                        scanResponseDTOdto.setId(s.getId());
                        scanResponseDTOdto.setStatus(s.getStatus());
                        scanResponseDTOdto.setStartedAt(s.getStartedAt());
                        scanResponseDTOdto.setCompletedAt(s.getCompletedAt());
                        scanResponseDTOdto.setQualityGate(s.getQualityGate());
                        scanResponseDTOdto.setMetrics(s.getMetrics());
                        scanResponseDTOdto.setLogFilePath(s.getLogFilePath());
                        return scanResponseDTOdto;
                    })
                    .toList();

            projectResponseDTO.setScanData(scans);
            return projectResponseDTO;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public SeveritySummaryDTO getScansIssue(UUID projectId) {

        List<ScanEntity> scans = scanRepository.findScansWithIssuesByProjectId(projectId);

        Map<String, Long> severityMap = new HashMap<>();

        for (ScanEntity scan : scans) {
            if (scan.getIssueData() == null) continue;
            for (IssueEntity issue : scan.getIssueData()) {
                if (issue == null || issue.getSeverity() == null) continue;

                String severity = issue.getSeverity();

                severityMap.put(severity, severityMap.getOrDefault(severity, 0L) + 1);
            }
        }

        return new SeveritySummaryDTO(severityMap);
    }





    public ScanResponseDTO SaveScan(ScanRequestsDTO req) {
        try {
            ProjectEntity project = projectRepository.findById(req.getProjectId())
                    .orElseThrow(() -> new RuntimeException("Project not found"));

            ScanEntity scan = new ScanEntity();
            scan.setProject(project);
            scan.setStatus(req.getStatus());
            scan.setStartedAt(req.getStartedAt());
            scan.setCompletedAt(req.getCompletedAt());
            scan.setQualityGate(req.getQualityGate());
            scan.setMetrics(req.getMetrics());
            scan.setLogFilePath(req.getLogFilePath());

            ScanEntity saved = scanRepository.save(scan);

            ScanResponseDTO dto = new ScanResponseDTO();
            dto.setId(saved.getId());
            dto.setStatus(saved.getStatus());
            dto.setStartedAt(saved.getStartedAt());
            dto.setCompletedAt(saved.getCompletedAt());
            dto.setQualityGate(saved.getQualityGate());
            dto.setMetrics(saved.getMetrics());
            dto.setLogFilePath(saved.getLogFilePath());

            return dto;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ScanResponseDTO updateScan(N8NResponseDTO req) {

        ScanEntity scan = scanRepository.findById(req.getScanId())
                .orElseThrow(() -> new RuntimeException("Scan not found"));

        if (req.getProjectId() != null) {
            ProjectEntity project = projectRepository.findById(req.getProjectId())
                    .orElseThrow(() -> new RuntimeException("Project not found"));
            scan.setProject(project);
            if (scan.getProject() != null && !scan.getProject().getId().equals(req.getProjectId())) {
                throw new RuntimeException("Project ID not match");
            }
        }

        ObjectMapper mapper = new ObjectMapper();

        Map<String, Object> metricsMap = mapper.convertValue(req.getMetrics(), new TypeReference<>() {});
        scan.setMetrics(metricsMap);

        scan.setStatus(req.getStatus());
        scan.setQualityGate(req.getQualityGate());
        scan.setLogFilePath(req.getLogFilePath());
        scan.setCompletedAt(req.getAnalyzedAt());
        String logFilePath = "scan-workspace/" + req.getScanId() + "/scan-report.md";
        scan.setLogFilePath(req.getLogFilePath());

        writeMarkdownFile(logFilePath, req.getMarkDown());
        ScanEntity updated = scanRepository.save(scan);

        if ("SUCCESS".equalsIgnoreCase(String.valueOf(req.getStatus()))) {
            List<IssueEntity> createdIssues = issueService.processFromMetrics(updated, metricsMap);

            if (updated.getIssueData() != null) {
                updated.getIssueData().clear();
                updated.getIssueData().addAll(createdIssues);
            }
        }


        ScanResponseDTO dto = new ScanResponseDTO();
        dto.setId(updated.getId());
        dto.setStatus(updated.getStatus());
        dto.setStartedAt(updated.getStartedAt());
        dto.setCompletedAt(updated.getCompletedAt());
        dto.setQualityGate(updated.getQualityGate());
        dto.setMetrics(updated.getMetrics());
        dto.setLogFilePath(updated.getLogFilePath());

        return dto;
    }
    public void writeMarkdownFile(String logFilePath, String markdown) {
        try {
            Path path = Path.of(logFilePath);

            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
            Files.writeString(
                    path,
                    markdown,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );

        } catch (Exception e) {
            throw new RuntimeException("Cannot write markdown file", e);
        }
    }
}
