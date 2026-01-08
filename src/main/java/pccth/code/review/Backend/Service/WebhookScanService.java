package pccth.code.review.Backend.Service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pccth.code.review.Backend.Config.WebhookConfig;
import pccth.code.review.Backend.DTO.Request.N8NRequestDTO;
import pccth.code.review.Backend.DTO.Response.N8NScanQueueResposneDTO;
import pccth.code.review.Backend.DTO.Response.ScanResponseDTO;
import pccth.code.review.Backend.Entity.ProjectEntity;
import pccth.code.review.Backend.Entity.ScanEntity;
import pccth.code.review.Backend.EnumType.ScanStatusEnum;
import pccth.code.review.Backend.Repository.ProjectRepository;
import pccth.code.review.Backend.Repository.ScanRepository;

import java.util.Date;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class WebhookScanService {

    private final ProjectRepository projectRepository;
    private final ScanRepository scanRepository;
    private final RedisQueueService redisQueueService;
    private final WebhookConfig webhookConfig;

    private final RestTemplate restTemplate = new RestTemplate();

    public WebhookScanService(
            ProjectRepository projectRepository,
            ScanRepository scanRepository,
            RedisQueueService redisQueueService,
            WebhookConfig webhookConfig
    ) {
        this.projectRepository = projectRepository;
        this.scanRepository = scanRepository;
        this.redisQueueService = redisQueueService;
        this.webhookConfig = webhookConfig;
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

        // push เข้า Redis queue
        redisQueueService.enqueueScan(request);

        // trigger n8n worker
        triggerN8nWorker();

        //response กลับ UI
        N8NScanQueueResposneDTO response = new N8NScanQueueResposneDTO();
        response.setScanId(scan.getId());
        response.setStatus(scan.getStatus());

        return response;
    }

    private void triggerN8nWorker() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("WEBHOOK-TOKEN", webhookConfig.getWebhookToken());

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        restTemplate.postForEntity(
                webhookConfig.getWorkerUrl(),
                entity,
                Void.class
        );
    }
    public ScanResponseDTO updateScan(UUID scanId, ScanResponseDTO req) {

        ScanEntity scan = scanRepository.findById(scanId)
                .orElseThrow(() -> new NoSuchElementException("Scan not found"));

        scan.setStatus(req.getStatus());
        scan.setCompletedAt(req.getCompletedAt());
        scan.setQualityGate(req.getQualityGate());
        scan.setMetrics(req.getMetrics());
        scan.setLogFilePath(req.getLogFilePath());

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
