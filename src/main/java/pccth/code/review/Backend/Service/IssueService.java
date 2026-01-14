package pccth.code.review.Backend.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pccth.code.review.Backend.DTO.Response.N8NResponseDTO;
import pccth.code.review.Backend.Entity.IssueEntity;
import pccth.code.review.Backend.Entity.ProjectEntity;
import pccth.code.review.Backend.Entity.ScanEntity;
import pccth.code.review.Backend.EnumType.ScanStatusEnum;
import pccth.code.review.Backend.Repository.IssueRepository;
import pccth.code.review.Backend.Repository.ProjectRepository;
import pccth.code.review.Backend.Repository.ScanRepository;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class IssueService {
    private final IssueRepository issueRepository;
    private final ScanRepository scanRepository;
    private final ProjectRepository projectRepository;
    private final ObjectMapper objectMapper;

    public IssueService(IssueRepository issueRepository, ScanRepository scanRepository, ProjectRepository projectRepository, ObjectMapper objectMapper) {
        this.issueRepository = issueRepository;
        this.scanRepository = scanRepository;
        this.projectRepository = projectRepository;
        this.objectMapper = objectMapper;
    }

    public List<IssueEntity> listIssues() {
        return issueRepository.findAll();
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

    @Transactional
    public void processScanResult(N8NResponseDTO result) {
        System.out.println("Processing scan result in IssueService: " + result);

        UUID projectId = result.getProjectId();
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with ID: " + projectId));

        ScanEntity scan = new ScanEntity();
        if (result.getScanId() != null) {
            scan.setId(result.getScanId());
        }
        scan.setProject(project);

        String statusStr = result.getStatus();
        if ("SUCCESS".equalsIgnoreCase(statusStr) || "COMPLETED".equalsIgnoreCase(statusStr)) {
            scan.setStatus(ScanStatusEnum.COMPLETED);
        } else if ("FAILED".equalsIgnoreCase(statusStr)) {
            scan.setStatus(ScanStatusEnum.FAILED);
        } else {
            scan.setStatus(ScanStatusEnum.PENDING);
        }

        scan.setQualityGate(result.getQualityGate());

        Map<String, Object> metricsMap = objectMapper.convertValue(result.getMetrics(), new TypeReference<Map<String, Object>>() {});
        scan.setMetrics(metricsMap);

        scan.setLogFilePath(result.getLogFilePath());

        if (result.getAnalyzedAt() != null) {
            scan.setCompletedAt(Date.from(result.getAnalyzedAt()));
        } else {
            scan.setCompletedAt(new Date());
        }

        if (result.getAnalysisDuration() != null && scan.getCompletedAt() != null) {
            long durationMs = result.getAnalysisDuration();
            scan.setStartedAt(new Date(scan.getCompletedAt().getTime() - durationMs));
        } else {
            scan.setStartedAt(new Date());
        }

        scanRepository.save(scan);
        System.out.println("Scan result saved successfully for project: " + project.getName());
    }
}
