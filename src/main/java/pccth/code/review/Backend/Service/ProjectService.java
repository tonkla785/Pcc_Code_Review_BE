package pccth.code.review.Backend.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import pccth.code.review.Backend.DTO.Request.RepositoryDTO;
import pccth.code.review.Backend.DTO.Response.RepositoryResponseDTO;
import pccth.code.review.Backend.Entity.ProjectEntity;
import pccth.code.review.Backend.EnumType.ProjectTypeEnum;
import pccth.code.review.Backend.Repository.ProjectRepository;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    // เพิ่ม repository
    public void addRepository(RepositoryDTO repository) {

        ProjectEntity project = new ProjectEntity();
        project.setId(UUID.randomUUID());
        project.setName(repository.getName());
        project.setRepositoryUrl(repository.getUrl());
        project.setProjectType(ProjectTypeEnum.valueOf(repository.getType()));
        project.setSonarProjectKey(repository.getName().trim().replaceAll("\\s+", "-"));
        project.setCreatedAt(new Date());

        projectRepository.save(project);
    }

    // ดึงข้อมูล repository ทั้งหมด
    public List<ProjectEntity> listProjects() {
        return projectRepository.findAll();
    }

    // ดึงข้อมูล repository ตาม id
    public RepositoryResponseDTO searchRepository(UUID id) {

        ProjectEntity entity = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Repository not found"));

        RepositoryResponseDTO dto = new RepositoryResponseDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setRepositoryUrl(entity.getRepositoryUrl());
        dto.setProjectType(entity.getProjectType().name());
        dto.setSonarProjectKey(entity.getSonarProjectKey());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        return dto;
    }

    // แก้ไข repository
    public void updateRepository(UUID id, RepositoryDTO repository) {
        ProjectEntity entity = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Repository not found")); // ตรวจสอบว่ามี id มั้ย

        entity.setName(repository.getName());
        entity.setRepositoryUrl(repository.getUrl());
        entity.setProjectType(ProjectTypeEnum.valueOf(repository.getType()));
        entity.setSonarProjectKey(repository.getName().trim().replaceAll("\\s+", "-"));
        entity.setUpdatedAt(new Date());

        projectRepository.save(entity);
    }

    // ลบ repository
    public void deleteRepository(UUID id) {
        ProjectEntity entity = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Repository not found")); // ตรวจสอบว่ามี id มั้ย
        projectRepository.delete(entity);
    }

}
