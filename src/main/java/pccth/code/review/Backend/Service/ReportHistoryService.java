package pccth.code.review.Backend.Service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pccth.code.review.Backend.DTO.Request.ReportHistoryRequestDTO;
import pccth.code.review.Backend.DTO.Response.ReportHistoryResponseDTO;
import pccth.code.review.Backend.Entity.ProjectEntity;
import pccth.code.review.Backend.Entity.ReportHistoryEntity;
import pccth.code.review.Backend.Entity.UserEntity;
import pccth.code.review.Backend.Repository.ProjectRepository;
import pccth.code.review.Backend.Repository.ReportHistoryRepository;
import pccth.code.review.Backend.Repository.UserRepository;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReportHistoryService {

    private final ReportHistoryRepository reportHistoryRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    public ReportHistoryService(
            ReportHistoryRepository reportHistoryRepository,
            UserRepository userRepository,
            ProjectRepository projectRepository) {
        this.reportHistoryRepository = reportHistoryRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
    }

    public List<ReportHistoryResponseDTO> getAllByUserId(UUID userId) {
        List<ReportHistoryEntity> reports = reportHistoryRepository.findByUserIdOrderByGeneratedAtDesc(userId);
        return reports.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<ReportHistoryResponseDTO> searchByProjectName(UUID userId, String keyword) {
        List<ReportHistoryEntity> reports = reportHistoryRepository.searchByProjectName(userId, keyword);
        return reports.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReportHistoryResponseDTO createReportHistory(UUID userId, ReportHistoryRequestDTO request) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ProjectEntity project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found"));

        ReportHistoryEntity report = new ReportHistoryEntity();
        report.setUser(user);
        report.setProject(project);
        report.setProjectName(request.getProjectName());
        report.setDateFrom(request.getDateFrom());
        report.setDateTo(request.getDateTo());
        report.setFormat(request.getFormat());
        report.setGeneratedBy(request.getGeneratedBy());
        report.setGeneratedAt(new Date());

        // Selected sections
        if (request.getIncludeQualityGate() != null) {
            report.setIncludeQualityGate(request.getIncludeQualityGate());
        }
        if (request.getIncludeIssueBreakdown() != null) {
            report.setIncludeIssueBreakdown(request.getIncludeIssueBreakdown());
        }
        if (request.getIncludeSecurityAnalysis() != null) {
            report.setIncludeSecurityAnalysis(request.getIncludeSecurityAnalysis());
        }

        // Snapshot data
        if (request.getSnapshotData() != null) {
            report.setSnapshotData(request.getSnapshotData());
        }

        if (request.getFileSizeBytes() != null) {
            report.setFileSizeBytes(request.getFileSizeBytes());
        }

        ReportHistoryEntity saved = reportHistoryRepository.save(report);
        return mapToResponseDTO(saved);
    }

    private ReportHistoryResponseDTO mapToResponseDTO(ReportHistoryEntity entity) {
        ReportHistoryResponseDTO dto = new ReportHistoryResponseDTO();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUser().getId());
        dto.setProjectId(entity.getProject().getId());
        dto.setProjectName(entity.getProjectName());
        dto.setDateFrom(entity.getDateFrom());
        dto.setDateTo(entity.getDateTo());
        dto.setFormat(entity.getFormat());
        dto.setGeneratedBy(entity.getGeneratedBy());
        dto.setGeneratedAt(entity.getGeneratedAt());
        dto.setIncludeQualityGate(entity.getIncludeQualityGate());
        dto.setIncludeIssueBreakdown(entity.getIncludeIssueBreakdown());
        dto.setIncludeSecurityAnalysis(entity.getIncludeSecurityAnalysis());
        dto.setSnapshotData(entity.getSnapshotData());
        dto.setFileSizeBytes(entity.getFileSizeBytes());
        return dto;
    }
}
