package pccth.code.review.Backend.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pccth.code.review.Backend.DTO.ScanWsEvent;
import pccth.code.review.Backend.DTO.Request.AngularSettingsDTO;
import pccth.code.review.Backend.DTO.Request.N8NRequestDTO;
import pccth.code.review.Backend.DTO.Request.SpringSettingsDTO;
import pccth.code.review.Backend.DTO.Response.N8NScanQueueResposneDTO;
import pccth.code.review.Backend.Entity.ProjectEntity;
import pccth.code.review.Backend.Entity.ScanEntity;
import pccth.code.review.Backend.EnumType.ScanStatusEnum;
import pccth.code.review.Backend.Integration.N8NWebhookClient;
import pccth.code.review.Backend.Messaging.ScanStatusPublisher;
import pccth.code.review.Backend.Repository.ProjectRepository;
import pccth.code.review.Backend.Repository.ScanRepository;

import java.util.Date;
import java.util.UUID;

@Service
public class WebhookScanService {

    private final ProjectRepository projectRepository;
    private final N8NWebhookClient n8NWebhookClient;
    private final ScanRepository scanRepository;
    private ScanStatusPublisher scanStatusPublisher;

    @Value("${webhook.token}")
    private String webhookToken;

    public WebhookScanService(
            ProjectRepository projectRepository,
            ScanRepository scanRepository,
            N8NWebhookClient n8NWebhookClient,
            ScanStatusPublisher scanStatusPublisher) {
        this.projectRepository = projectRepository;
        this.n8NWebhookClient = n8NWebhookClient;
        this.scanRepository = scanRepository;
        this.scanStatusPublisher = scanStatusPublisher;
    }

    @Transactional
    public N8NScanQueueResposneDTO triggerScan(
            UUID projectId,
            String branch,
            String sonarToken,
            AngularSettingsDTO angularSettings,
            SpringSettingsDTO springSettings) {
        // หา project
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        // สร้าง Scan
        ScanEntity scan = new ScanEntity();
        scan.setProject(project);
        scan.setStatus(ScanStatusEnum.PENDING);
        scan.setStartedAt(new Date());
        scanRepository.save(scan);

        // เตรียม payload
        N8NRequestDTO request = new N8NRequestDTO();
        request.setProjectId(project.getId());
        request.setScanId(scan.getId());
        request.setRepositoryUrl(project.getRepositoryUrl());
        request.setProjectType(project.getProjectType());
        request.setSonarProjectKey(project.getSonarProjectKey());
        request.setBranch(branch);

        // Settings จาก frontend
        request.setSonarToken(sonarToken);
        request.setAngularSettings(angularSettings);
        request.setSpringSettings(springSettings);

        // Token สำหรับ n8n authen กลับมา Spring Boot
        request.setWebhookToken(webhookToken);

        // trigger n8n worker
        try {
            n8NWebhookClient.postToN8N(request);
        } catch (Exception e) {
            throw new RuntimeException("Trigger n8n scan failed", e);
        }

        scanStatusPublisher.publish(
                new ScanWsEvent(project.getId(), ScanStatusEnum.PENDING, scan.getId())
        );

        // response กลับ UI
        N8NScanQueueResposneDTO response = new N8NScanQueueResposneDTO();
        response.setScanId(scan.getId());
        response.setStatus(scan.getStatus());

        return response;
    }
}