package pccth.code.review.Backend.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import pccth.code.review.Backend.DTO.Request.RepositoryDTO;
import pccth.code.review.Backend.DTO.Response.RegisterResponseDTO;
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
    public RepositoryResponseDTO addRepository(RepositoryDTO repository) {
        try {
            ProjectEntity project = new ProjectEntity();
            project.setId(UUID.randomUUID());
            project.setName(repository.getName());
            project.setRepositoryUrl(repository.getUrl());
            project.setProjectType(ProjectTypeEnum.valueOf(repository.getType()));
            project.setSonarProjectKey(repository.getName().trim().replaceAll("\\s+", "-"));
            project.setCreatedAt(new Date());

            projectRepository.save(project);

            RepositoryResponseDTO response = new RepositoryResponseDTO();
            response.setMessage("Repository added successfully");
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Server Error", e);
        }
    }

    // ดึงข้อมูล repository ทั้งหมด
    public List<ProjectEntity> listProjects() {
        return projectRepository.findAll();
    }

    // ดึงข้อมูล repository ตาม id
    public ProjectEntity searchRepository(UUID id) {

        ProjectEntity entity = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Repository not found"));

        return entity;
    }

    // แก้ไข repository
    public RepositoryResponseDTO updateRepository(UUID id, RepositoryDTO repository) {
        ProjectEntity entity = projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Repository not found")); // ตรวจสอบว่ามี id มั้ย

        try {
            entity.setName(repository.getName());
            entity.setRepositoryUrl(repository.getUrl());
            entity.setProjectType(ProjectTypeEnum.valueOf(repository.getType()));
            entity.setSonarProjectKey(repository.getName().trim().replaceAll("\\s+", "-"));
            entity.setUpdatedAt(new Date());

            projectRepository.save(entity);

            RepositoryResponseDTO response = new RepositoryResponseDTO();
            response.setMessage("Repository added successfully");
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
            projectRepository.delete(entity);

            RepositoryResponseDTO response = new RepositoryResponseDTO();
            response.setMessage("Repository deleted successfully");
            return response;

        } catch (Exception e) {
            throw new RuntimeException("Server Error", e);
        }

    }

}
