package pccth.code.review.Backend.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import pccth.code.review.Backend.DTO.Request.RepositoryDTO;
import pccth.code.review.Backend.DTO.Response.ProjectResponseDTO;
import pccth.code.review.Backend.DTO.Response.RepositoryResponseDTO;
import pccth.code.review.Backend.DTO.Response.ScanResponseDTO;
import pccth.code.review.Backend.Entity.ProjectEntity;
import pccth.code.review.Backend.Entity.ScanEntity;
import pccth.code.review.Backend.EnumType.ProjectTypeEnum;
import pccth.code.review.Backend.Repository.ProjectRepository;
import pccth.code.review.Backend.Messaging.ProjectBroadcastPublisher;
import pccth.code.review.Backend.Messaging.ProjectBroadcastPublisher.ProjectChangeEvent;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectBroadcastPublisher projectBroadcastPublisher;

    public ProjectService(ProjectRepository projectRepository, ProjectBroadcastPublisher projectBroadcastPublisher) {
        this.projectRepository = projectRepository;
        this.projectBroadcastPublisher = projectBroadcastPublisher;
    }

    // เพิ่ม repository
    public RepositoryResponseDTO addRepository(RepositoryDTO repository) {
        try {
            ProjectEntity project = new ProjectEntity();
            project.setName(repository.getName());
            project.setRepositoryUrl(repository.getUrl());
            project.setProjectType(ProjectTypeEnum.valueOf(repository.getType()));
            project.setSonarProjectKey(repository.getName().trim().replaceAll("\\s+", "-"));
            project.setCostPerDay(repository.getCostPerDay());
            project.setCreatedAt(new Date());

            projectRepository.save(project);

            // Broadcast project added to all users
            projectBroadcastPublisher.broadcastProjectChange(
                    new ProjectChangeEvent("ADDED", project.getId(), project.getName()));

            RepositoryResponseDTO response = new RepositoryResponseDTO();
            response.setProjectId(project.getId());
            response.setMessage("Repository added successfully");
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Server Error", e);
        }
    }

    // ดึงข้อมูล repository ทั้งหมด
    public List<ProjectResponseDTO> listProjects() {
        List<ProjectEntity> projects = projectRepository.findAll();
        List<ProjectResponseDTO> projectDto = new ArrayList<>();

        for (ProjectEntity p : projects) {
            ProjectResponseDTO dto = new ProjectResponseDTO();
            dto.setId(p.getId());
            dto.setName(p.getName());
            dto.setRepositoryUrl(p.getRepositoryUrl());
            dto.setProjectType(p.getProjectType());
            dto.setSonarProjectKey(p.getSonarProjectKey());
            dto.setCostPerDay(p.getCostPerDay());
            dto.setCreatedAt(p.getCreatedAt());
            dto.setUpdatedAt(p.getUpdatedAt());

            List<ScanResponseDTO> scanDto = new ArrayList<>();

            for (ScanEntity scan : p.getScanData()) {
                ScanResponseDTO scanResponseDTO = new ScanResponseDTO();
                scanResponseDTO.setId(scan.getId());
                scanResponseDTO.setStatus(scan.getStatus());
                scanResponseDTO.setStartedAt(scan.getStartedAt());
                scanResponseDTO.setCompletedAt(scan.getCompletedAt());
                scanResponseDTO.setQualityGate(scan.getQualityGate());
                scanResponseDTO.setMetrics(scan.getMetrics());
                scanResponseDTO.setLogFilePath(scan.getLogFilePath());
                scanDto.add(scanResponseDTO);
            }

            dto.setScanData(scanDto);
            projectDto.add(dto);
        }
        return projectDto;
    }

    // ดึงข้อมูล repository ตาม id
    public ProjectResponseDTO searchRepository(UUID id) {

        ProjectEntity entity = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Repository not found"));

        ProjectResponseDTO response = new ProjectResponseDTO();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setRepositoryUrl(entity.getRepositoryUrl());
        response.setProjectType(entity.getProjectType());
        response.setSonarProjectKey(entity.getSonarProjectKey());
        response.setCostPerDay(entity.getCostPerDay());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        return response;
    }

    // แก้ไข repository จาก user
    public RepositoryResponseDTO updateRepository(UUID id, RepositoryDTO repository) {
        ProjectEntity entity = projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Repository not found")); // ตรวจสอบว่ามี id มั้ย
        try {
            entity.setName(repository.getName());
            entity.setRepositoryUrl(repository.getUrl());
            entity.setProjectType(ProjectTypeEnum.valueOf(repository.getType()));
            entity.setCostPerDay(repository.getCostPerDay());
            entity.setUpdatedAt(new Date());
            projectRepository.save(entity);

            // Broadcast project updated to all users
            projectBroadcastPublisher.broadcastProjectChange(
                    new ProjectChangeEvent("UPDATED", entity.getId(), entity.getName()));

            RepositoryResponseDTO response = new RepositoryResponseDTO();
            response.setMessage("Repository updated successfully");
            return response;

        } catch (Exception e) {
            throw new RuntimeException("Server Error", e);
        }
    }

    // แก้ไข repository จากการ scan
    public RepositoryResponseDTO updateScanAt(UUID id) {
        ProjectEntity entity = projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Repository not found")); // ตรวจสอบว่ามี id มั้ย
        try {
            entity.setUpdatedAt(new Date());

            projectRepository.save(entity);

            RepositoryResponseDTO response = new RepositoryResponseDTO();
            response.setMessage("Repository updated successfully");
            return response;

        } catch (Exception e) {
            throw new RuntimeException("Server Error", e);
        }
    }

    // ลบ repository
    public RepositoryResponseDTO deleteRepository(UUID id) {
        ProjectEntity entity = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Repository not found")); // ตรวจสอบว่ามี id มั้ย
        try {
            String projectName = entity.getName();
            projectRepository.delete(entity);

            // Broadcast project deleted to all users
            projectBroadcastPublisher.broadcastProjectChange(
                    new ProjectChangeEvent("DELETED", id, projectName));

            RepositoryResponseDTO response = new RepositoryResponseDTO();
            response.setMessage("Repository deleted successfully");
            return response;

        } catch (Exception e) {
            throw new RuntimeException("Server Error", e);
        }

    }

    public ProjectResponseDTO mapToProjectResponseDTO(ProjectEntity p) {
        if (p == null)
            return null;

        ProjectResponseDTO dto = new ProjectResponseDTO();
        dto.setId(p.getId());
        dto.setName(p.getName());
        return dto;
    }

}