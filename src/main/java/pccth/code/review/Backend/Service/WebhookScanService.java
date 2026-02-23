package pccth.code.review.Backend.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pccth.code.review.Backend.DTO.Request.*;
import pccth.code.review.Backend.DTO.Response.RecommendFixByAiResponseDTO;
import pccth.code.review.Backend.DTO.ScanWsEvent;
import pccth.code.review.Backend.DTO.Response.N8NScanQueueResposneDTO;
import pccth.code.review.Backend.Entity.IssueDetailEntity;
import pccth.code.review.Backend.Entity.IssueEntity;
import pccth.code.review.Backend.Entity.ProjectEntity;
import pccth.code.review.Backend.Entity.ScanEntity;
import pccth.code.review.Backend.EnumType.ScanStatusEnum;
import pccth.code.review.Backend.Integration.N8NWebhookClient;
import pccth.code.review.Backend.Messaging.ScanStatusPublisher;
import pccth.code.review.Backend.Repository.IssueDetailRepository;
import pccth.code.review.Backend.Repository.IssueRepository;
import pccth.code.review.Backend.Repository.ProjectRepository;
import pccth.code.review.Backend.Repository.ScanRepository;

import java.util.Date;
import java.util.UUID;

@Service
public class WebhookScanService {

    private final IssueRepository issueRepository;
    private final IssueDetailRepository issueDetailRepository;
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
            ScanStatusPublisher scanStatusPublisher,
            IssueRepository issueRepository,
            IssueDetailRepository issueDetailRepository) {
        this.projectRepository = projectRepository;
        this.n8NWebhookClient = n8NWebhookClient;
        this.scanRepository = scanRepository;
        this.scanStatusPublisher = scanStatusPublisher;
        this.issueRepository = issueRepository;
        this.issueDetailRepository = issueDetailRepository;
    }

    @Transactional
    public N8NScanQueueResposneDTO triggerScan(
            UUID projectId,
            String branch,
            String sonarToken,
            String gitToken,
            AngularSettingsDTO angularSettings,
            SpringSettingsDTO springSettings,
            QualityGateSettingsDTO qualityGateSettings) {
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
        request.setGitToken(gitToken);

        // Settings จาก frontend
        request.setSonarToken(sonarToken);
        request.setAngularSettings(angularSettings);
        request.setSpringSettings(springSettings);
        request.setQualityGateSettings(qualityGateSettings);


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

    @Transactional
    public RecommendFixByAiResponseDTO recommendFixByAi(RecommendAiRequestDTO recommendAiRequestDTO) {
        ProjectEntity project = projectRepository.findById(recommendAiRequestDTO.getProjectId())
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        IssueEntity issue = issueRepository.findById(recommendAiRequestDTO.getIssueId())
                .orElseThrow(() -> new IllegalArgumentException("issue not found"));

        IssueDetailEntity issueDetail = issueDetailRepository.findById(recommendAiRequestDTO.getIssueId())
                .orElseThrow(() -> new IllegalArgumentException("issueDetail not found"));

        RecommendAiN8NRequestDTO requestDTO = new RecommendAiN8NRequestDTO();
        requestDTO.setIssueId(issue.getId());
        requestDTO.setProjectId(project.getId());
        requestDTO.setRuleKey(issue.getRuleKey());
        requestDTO.setMessage(issue.getMessage());
        requestDTO.setDescription(issueDetail.getDescription());
        requestDTO.setVulnerableCode(issueDetail.getVulnerableCode());
        requestDTO.setRecommendedFix(issueDetail.getRecommendedFix());
        requestDTO.setWebhookToken(webhookToken);

        issueDetail.setStatus("PENDING");


        try {
            n8NWebhookClient.postIssueToN8N(requestDTO);
        } catch (Exception e) {
            throw new RuntimeException("Trigger n8n ai failed", e);
        }

        issueDetailRepository.save(issueDetail);
        RecommendFixByAiResponseDTO response = new RecommendFixByAiResponseDTO();
        response.setIssueId(recommendAiRequestDTO.getIssueId());
        response.setMessage(issueDetail.getStatus());
        return response;
    }
}