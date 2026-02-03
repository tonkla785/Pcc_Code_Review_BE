package pccth.code.review.Backend.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import pccth.code.review.Backend.DTO.Response.*;
import pccth.code.review.Backend.DTO.ScanWsEvent;
import pccth.code.review.Backend.Entity.IssueEntity;
import pccth.code.review.Backend.Entity.ProjectEntity;
import pccth.code.review.Backend.Entity.ScanEntity;
import pccth.code.review.Backend.Entity.UserEntity;
import pccth.code.review.Backend.Messaging.ScanStatusPublisher;
import pccth.code.review.Backend.Repository.ProjectRepository;
import pccth.code.review.Backend.Repository.ScanIssueRepository;
import pccth.code.review.Backend.Repository.ScanRepository;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.*;

@Service
public class ScanService {

    @Autowired
    private ScanRepository scanRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private ScanIssueRepository scanIssueRepository;
    @Autowired
    private ScanStatusPublisher scanStatusPublisher;

    public ScanResponseDTO getScansById(UUID scanId) {

        ScanEntity scan = scanRepository.findById(scanId)
                .orElseThrow(() -> new RuntimeException("Scan not found"));

        ProjectEntity project = scan.getProject();

        ProjectResponseDTO projectDTO = new ProjectResponseDTO();
        projectDTO.setId(project.getId());
        projectDTO.setName(project.getName());
        projectDTO.setRepositoryUrl(project.getRepositoryUrl());
        projectDTO.setProjectType(project.getProjectType());
        projectDTO.setSonarProjectKey(project.getSonarProjectKey());
        projectDTO.setCreatedAt(project.getCreatedAt());
        projectDTO.setUpdatedAt(project.getUpdatedAt());

        ScanResponseDTO dto = new ScanResponseDTO();
        dto.setId(scan.getId());
        dto.setProject(projectDTO);
        dto.setStatus(scan.getStatus());
        dto.setStartedAt(scan.getStartedAt());
        dto.setCompletedAt(scan.getCompletedAt());
        dto.setQualityGate(scan.getQualityGate());
        dto.setMetrics(scan.getMetrics());
        dto.setLogFilePath(scan.getLogFilePath());
        dto.setIssueData(
                scan.getScanIssues().stream()
                        .map(scanIssue -> {
                            IssueEntity issue = scanIssue.getIssue();
                            UserEntity user = issue.getAssignedTo();
                            IssuesResponseDTO idto = new IssuesResponseDTO();
                            if (issue.getAssignedTo() != null) {
                                UserResponseDTO userDTO = new UserResponseDTO();
                                userDTO.setId(user.getId());
                                userDTO.setUsername(user.getUsername());
                                userDTO.setEmail(user.getEmail());
                                userDTO.setRole(user.getRole());
                                userDTO.setPhone(user.getPhone());
                                userDTO.setCreateAt(user.getCreateAt());
                                idto.setAssignedTo(userDTO);
                            }
                            idto.setId(issue.getId());
                            idto.setScanId(scan.getId());
                            idto.setProjectId(issue.getProject().getId());
                            idto.setLine(issue.getLine());
                            idto.setRuleKey(issue.getRuleKey());
                            idto.setIssueKey(issue.getIssueKey());
                            idto.setType(issue.getType());
                            idto.setSeverity(issue.getSeverity());
                            idto.setComponent(issue.getComponent());
                            idto.setLine(issue.getLine());
                            idto.setMessage(issue.getMessage());
                            idto.setStatus(issue.getStatus());
                            idto.setCreatedAt(issue.getCreatedAt());

                            idto.setCommentData(
                                    issue.getCommentData().stream()
                                            .map(comment -> {
                                                CommentResponseDTO cdto = new CommentResponseDTO();
                                                UserEntity users = comment.getUser();
                                                UserResponseDTO userDTO = new UserResponseDTO();
                                                userDTO.setId(users.getId());
                                                userDTO.setUsername(users.getUsername());
                                                userDTO.setEmail(users.getEmail());
                                                userDTO.setPhone(users.getPhone());
                                                userDTO.setRole(users.getRole());
                                                userDTO.setCreateAt(comment.getCreatedAt());
                                                cdto.setId(comment.getId());
                                                cdto.setComment(comment.getComment());
                                                cdto.setCreatedAt(comment.getCreatedAt());
                                                cdto.setIssue(issue.getId());
                                                cdto.setUser(userDTO);
                                                return cdto;
                                            })
                                            .toList());

                            return idto;
                        })
                        .toList());
        return dto;
    }

    public List<ScanResponseDTO> getScansAll() {

        List<ScanEntity> scans = scanRepository.findAllByOrderByStartedAtAsc();
        List<ScanResponseDTO> result = new ArrayList<>();

        for (ScanEntity scan : scans) {

            ProjectEntity project = scan.getProject();

            ProjectResponseDTO projectDTO = new ProjectResponseDTO();
            projectDTO.setId(project.getId());
            projectDTO.setName(project.getName());
            projectDTO.setRepositoryUrl(project.getRepositoryUrl());
            projectDTO.setProjectType(project.getProjectType());
            projectDTO.setSonarProjectKey(project.getSonarProjectKey());
            projectDTO.setCreatedAt(project.getCreatedAt());
            projectDTO.setUpdatedAt(project.getUpdatedAt());

            ScanResponseDTO dto = new ScanResponseDTO();
            dto.setId(scan.getId());
            dto.setProject(projectDTO);
            dto.setStatus(scan.getStatus());
            dto.setStartedAt(scan.getStartedAt());
            dto.setCompletedAt(scan.getCompletedAt());
            dto.setQualityGate(scan.getQualityGate());
            dto.setMetrics(scan.getMetrics());
            dto.setLogFilePath(scan.getLogFilePath());

            result.add(dto);
        }

        return result;
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

        Map<String, Object> metricsMap = mapper.convertValue(req.getMetrics(), new TypeReference<>() {
        });

        // Add analysisLogs to metrics if present
        if (req.getAnalysisLogs() != null && !req.getAnalysisLogs().isEmpty()) {
            metricsMap.put("analysisLogs", req.getAnalysisLogs());
        }

        scan.setMetrics(metricsMap);

        scan.setStatus(req.getStatus());
        scan.setQualityGate(req.getQualityGate());
        scan.setLogFilePath(req.getLogFilePath());
        scan.setCompletedAt(new Date());
        String logFilePath = "scan-workspace/" + req.getScanId() + "/scan-report.md";
        scan.setLogFilePath(logFilePath);

        ScanEntity updated = scanRepository.save(scan);

        ScanResponseDTO dto = new ScanResponseDTO();
        dto.setId(updated.getId());
        dto.setStatus(updated.getStatus());
        dto.setStartedAt(updated.getStartedAt());
        dto.setCompletedAt(updated.getCompletedAt());
        dto.setQualityGate(updated.getQualityGate());
        dto.setMetrics(updated.getMetrics());
        dto.setLogFilePath(updated.getLogFilePath());

        scanStatusPublisher.publish(
                new ScanWsEvent(req.getProjectId(), req.getStatus(),req.getScanId())
        );

        return dto;
    }
}
