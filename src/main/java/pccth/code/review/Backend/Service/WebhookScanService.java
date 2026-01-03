package pccth.code.review.Backend.Service;

import org.springframework.stereotype.Service;
import pccth.code.review.Backend.DTO.Request.N8NRequestDTO;
import pccth.code.review.Backend.DTO.Response.N8NScanQueueResposneDTO;
import pccth.code.review.Backend.Entity.ProjectEntity;
import pccth.code.review.Backend.Entity.ScanEntity;
import pccth.code.review.Backend.EnumType.ScanStatusEnum;
import pccth.code.review.Backend.Repository.ProjectRepository;
import pccth.code.review.Backend.Repository.ScanRepository;

import java.util.Date;
import java.util.UUID;

@Service
public class WebhookScanService {

    private final ProjectRepository projectRepository;
    private final ScanRepository scanRepository;
    private final RedisQueueService redisQueueService;

    public WebhookScanService(
            ProjectRepository projectRepository,
            ScanRepository scanRepository,
            RedisQueueService redisQueueService
    ) {
        this.projectRepository = projectRepository;
        this.scanRepository = scanRepository;
        this.redisQueueService = redisQueueService;
    }

    public N8NScanQueueResposneDTO triggerScan(UUID projectId, String branch) {

        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        // สร้าง Scan ใหม่
        ScanEntity scan = new ScanEntity();
        scan.setProject(project);
        scan.setStatus(ScanStatusEnum.pending);
        scan.setStartedAt(new Date());

        scanRepository.save(scan);

        // เตรียม payload ส่งไป n8n
        N8NRequestDTO request = new N8NRequestDTO();
        request.setProjectId(project.getId());
        request.setScanId(scan.getId());
        request.setRepositoryUrl(project.getRepositoryUrl());
        request.setProjectType(project.getProjectType());
        request.setSonarProjectKey(project.getSonarProjectKey());
        request.setBranch(branch);

        // เข้า Redis Queue
        redisQueueService.enqueueScan(request);

        N8NScanQueueResposneDTO response = new N8NScanQueueResposneDTO();
        response.setScanId(scan.getId());
        response.setStatus(scan.getStatus());

        return response;
    }
}
