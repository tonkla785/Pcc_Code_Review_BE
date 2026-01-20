package pccth.code.review.Backend.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pccth.code.review.Backend.DTO.Request.N8NRequestDTO;
import pccth.code.review.Backend.DTO.Response.N8NScanQueueResposneDTO;
import pccth.code.review.Backend.Entity.ProjectEntity;
import pccth.code.review.Backend.Entity.ScanEntity;
import pccth.code.review.Backend.EnumType.ScanStatusEnum;
import pccth.code.review.Backend.Integration.N8NWebhookClient;
import pccth.code.review.Backend.Repository.ProjectRepository;
import pccth.code.review.Backend.Repository.ScanRepository;

import java.util.Date;
import java.util.UUID;

@Service
public class WebhookScanService {

    private final ProjectRepository projectRepository;
    private final N8NWebhookClient n8NWebhookClient;
    private final ScanRepository scanRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    public WebhookScanService(
            ProjectRepository projectRepository,
            ScanRepository scanRepository,
            N8NWebhookClient n8NWebhookClient
    ) {
        this.projectRepository = projectRepository;
        this.n8NWebhookClient = n8NWebhookClient;
        this.scanRepository = scanRepository;
    }

    public N8NScanQueueResposneDTO triggerScan(UUID projectId, String branch) {

        // หา project
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        // สร้าง Scan
        ScanEntity scan = new ScanEntity();
        scan.setProject(project);
        scan.setStatus(ScanStatusEnum.PENDING);
        scan.setStartedAt(new Date());
        scanRepository.save(scan);

        // เตรียม payload (ไว้ให้ n8n pop จาก redis)
        N8NRequestDTO request = new N8NRequestDTO();
        request.setProjectId(project.getId());
        request.setScanId(scan.getId());
        request.setRepositoryUrl(project.getRepositoryUrl());
        request.setProjectType(project.getProjectType());
        request.setSonarProjectKey(project.getSonarProjectKey());
        request.setBranch(branch);

        // trigger n8n worker
        n8NWebhookClient.postToN8N(request);

        //response กลับ UI
        N8NScanQueueResposneDTO response = new N8NScanQueueResposneDTO();
        response.setScanId(scan.getId());
        response.setStatus(scan.getStatus());

        return response;
    }
}