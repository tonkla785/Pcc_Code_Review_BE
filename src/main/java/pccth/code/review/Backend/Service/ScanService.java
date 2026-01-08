package pccth.code.review.Backend.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pccth.code.review.Backend.DTO.Request.ScanRequestsDTO;
import pccth.code.review.Backend.DTO.Response.*;
import pccth.code.review.Backend.Entity.IssueEntity;
import pccth.code.review.Backend.Entity.ProjectEntity;
import pccth.code.review.Backend.Entity.ScanEntity;
import pccth.code.review.Backend.Repository.IssueRepository;
import pccth.code.review.Backend.Repository.ProjectRepository;
import pccth.code.review.Backend.Repository.ScanRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class ScanService {

    @Autowired
    private ScanRepository scanRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private IssueRepository issueRepository;

    public List<ScanResponseDTO> getScansHistory(UUID projectId) {
        try {
            List<ScanEntity> scans = scanRepository.findByProjectId(projectId);
            List<ScanResponseDTO> responseDTOs = new ArrayList<>();
            for (ScanEntity e : scans) {
                ScanResponseDTO scanResponseDTO = new ScanResponseDTO();
                scanResponseDTO.setId(e.getId());
                if (e.getProject() != null) {
                    ProjectEntity project = e.getProject();
                    ProjectResponseDTO projectResponseDTO = new ProjectResponseDTO();
                    projectResponseDTO.setId(project.getId());
                    projectResponseDTO.setName(project.getName());
                    projectResponseDTO.setRepositoryUrl(project.getRepositoryUrl());
                    projectResponseDTO.setProjectType(project.getProjectType());
                    projectResponseDTO.setSonarProjectKey(project.getSonarProjectKey());
                    projectResponseDTO.setCreatedAt(project.getCreatedAt());
                    projectResponseDTO.setUpdatedAt(project.getUpdatedAt());
                    scanResponseDTO.setProject(projectResponseDTO);
                }
                scanResponseDTO.setStatus(e.getStatus());
                scanResponseDTO.setStartedAt(e.getStartedAt());
                scanResponseDTO.setCompletedAt(e.getCompletedAt());
                scanResponseDTO.setQualityGate(e.getQualityGate());
                scanResponseDTO.setMetrics(e.getMetrics());
                scanResponseDTO.setLogFilePath(e.getLogFilePath());
                responseDTOs.add(scanResponseDTO);
            }
            return responseDTOs;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public ScanResponseDTO getScansById(UUID scanId) {
        try {
            ScanEntity scans = scanRepository.findById(scanId).orElseThrow(() -> new RuntimeException("Scan not found"));
            ScanResponseDTO scanResponseDTO = new ScanResponseDTO();
            scanResponseDTO.setId(scans.getId());
            if (scans.getProject() != null) {
                ProjectResponseDTO projectDTO = new ProjectResponseDTO();
                projectDTO.setId(scans.getProject().getId());
                projectDTO.setName(scans.getProject().getName());
                projectDTO.setRepositoryUrl(scans.getProject().getRepositoryUrl());
                projectDTO.setProjectType(scans.getProject().getProjectType());
                projectDTO.setSonarProjectKey(scans.getProject().getSonarProjectKey());
                projectDTO.setCreatedAt(scans.getProject().getCreatedAt());
                projectDTO.setUpdatedAt(scans.getProject().getUpdatedAt());
                scanResponseDTO.setProject(projectDTO);
            }
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
//                        idto.setSeverity(issue.getSeverity());
                        idto.setComponent(issue.getComponent());
                        idto.setMessage(issue.getMessage());
//                        idto.setAssignedTo(issue.getAssignedTo().getId());
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
                                }).toList()
                        );
                        return idto;
                    }).toList()
            );
            return scanResponseDTO;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public List<ScanResponseDTO> getScansAll() {
        try {
            List<ScanEntity> scans = scanRepository.findAll();
            List<ScanResponseDTO> responseDTOs = new ArrayList<>();
            for (ScanEntity e : scans) {
                ScanResponseDTO scanResponseDTO = new ScanResponseDTO();
                if(e.getProject() != null) {
                    ProjectEntity project = e.getProject();
                    ProjectResponseDTO projectResponseDTO = new ProjectResponseDTO();
                    projectResponseDTO.setId(project.getId());
                    projectResponseDTO.setName(project.getName());
                    projectResponseDTO.setRepositoryUrl(project.getRepositoryUrl());
                    projectResponseDTO.setProjectType(project.getProjectType());
                    projectResponseDTO.setSonarProjectKey(project.getSonarProjectKey());
                    projectResponseDTO.setCreatedAt(project.getCreatedAt());
                    projectResponseDTO.setUpdatedAt(project.getUpdatedAt());
                    scanResponseDTO.setProject(projectResponseDTO);
                }
                scanResponseDTO.setId(e.getId());
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
//                            idto.setSeverity(issue.getSeverity());
                            idto.setComponent(issue.getComponent());
                            idto.setMessage(issue.getMessage());
//                            idto.setAssignedTo(issue.getAssignedTo().getId());
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
                                    }).toList()
                            );
                            return idto;
                        }).toList()
                );
                responseDTOs.add(scanResponseDTO);
            }
            return responseDTOs;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
    public ScanResponseDTO getScansLog(UUID scanId) {
        try {
            ScanEntity scans = scanRepository.findById(scanId).orElseThrow(() -> new RuntimeException("Scan not found"));
            ScanResponseDTO scanResponseDTO = new ScanResponseDTO();
            scanResponseDTO.setLogFilePath(scans.getLogFilePath());
            return scanResponseDTO;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public ProjectResponseDTO getProject(UUID projectId) {
        try {
            ProjectEntity projectEntity = projectRepository.findById(projectId).orElseThrow(() -> new RuntimeException("Project not found"));
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
                        scanResponseDTOdto.setProjectId(s.getProject().getId());
                        scanResponseDTOdto.setStatus(s.getStatus());
                        scanResponseDTOdto.setStartedAt(s.getStartedAt());
                        scanResponseDTOdto.setCompletedAt(s.getCompletedAt());
                        scanResponseDTOdto.setQualityGate(s.getQualityGate());
                        scanResponseDTOdto.setMetrics(s.getMetrics());
                        scanResponseDTOdto.setLogFilePath(s.getLogFilePath());
                        scanResponseDTOdto.setIssueData(
                                s.getIssueData().stream().map(issue -> {
                                    IssuesReponseDTO idto = new IssuesReponseDTO();
                                    idto.setId(issue.getId());
                                    idto.setScanId(issue.getScan().getId());
                                    idto.setIssueKey(issue.getIssueKey());
                                    idto.setType(issue.getType());
//                                    idto.setSeverity(issue.getSeverity());
                                    idto.setComponent(issue.getComponent());
                                    idto.setMessage(issue.getMessage());
                                    if (issue.getAssignedTo() != null) {
                                        UserResponseDTO userResponseDTO = new UserResponseDTO();
                                        userResponseDTO.setId(issue.getAssignedTo().getId());
                                        userResponseDTO.setUsername(issue.getAssignedTo().getUsername());
                                        userResponseDTO.setEmail(issue.getAssignedTo().getEmail());
                                        userResponseDTO.setRole(issue.getAssignedTo().getRole());
                                        userResponseDTO.setPhone(issue.getAssignedTo().getPhone());
                                        userResponseDTO.setCreateAt(issue.getAssignedTo().getCreateAt());
                                        idto.setAssignedTo(userResponseDTO);
                                    }
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
                                            }).toList()
                                    );
                                    return idto;
                                }).toList()
                        );
                        return scanResponseDTOdto;
                    })
                    .toList();

            projectResponseDTO.setScanData(scans);
            return projectResponseDTO;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public List<IssueStatusCountDTO> getScansIssue(UUID scanId) {

        return issueRepository.countIssuesBySeverityInProject(scanId);
    }

    public ScanResponseDTO SaveScan(ScanRequestsDTO req) {
        try{
        ProjectEntity project = projectRepository.findById(req.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found"));

        ScanEntity scan = new ScanEntity();
        scan.setProject(project);
        scan.setStatus(req.getStatus());

//        scan.setStartedAt(req.getStartedAt());
//        scan.setCompletedAt(req.getCompletedAt());
//        scan.setQualityGate(req.getQualityGate());
//        scan.setMetrics(req.getMetrics());
//        scan.setLogFilePath(req.getLogFilePath());

        ScanEntity saved = scanRepository.save(scan);
        ScanResponseDTO dto = new ScanResponseDTO();
            if (scan.getProject() != null) {
                ProjectResponseDTO projectDTO = new ProjectResponseDTO();
                projectDTO.setId(scan.getProject().getId());
                projectDTO.setName(scan.getProject().getName());
                dto.setProject(projectDTO);
            }
        dto.setId(saved.getId());
        dto.setStatus(saved.getStatus());
        dto.setStartedAt(saved.getStartedAt());
        dto.setCompletedAt(saved.getCompletedAt());
        dto.setQualityGate(saved.getQualityGate());
        dto.setMetrics(saved.getMetrics());
        dto.setLogFilePath(saved.getLogFilePath());

        return dto;
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public ScanResponseDTO updateScan(UUID scanId, ScanResponseDTO req) {

        ScanEntity scan = scanRepository.findById(scanId)
                .orElseThrow(() -> new NoSuchElementException("Scan not found"));

        scan.setStatus(req.getStatus());


        ScanEntity saved = scanRepository.save(scan);

        ScanResponseDTO dto = new ScanResponseDTO();
        dto.setId(saved.getId());
//        dto.setProjectId(saved.getProject().getId());
        dto.setStatus(saved.getStatus());
        dto.setStartedAt(saved.getStartedAt());
        dto.setCompletedAt(saved.getCompletedAt());
        dto.setQualityGate(saved.getQualityGate());
        dto.setMetrics(saved.getMetrics());
        dto.setLogFilePath(saved.getLogFilePath());

        return dto;
    }


}

