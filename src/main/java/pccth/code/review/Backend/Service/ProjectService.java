package pccth.code.review.Backend.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import pccth.code.review.Backend.DTO.Request.RepositoryDTO;
import pccth.code.review.Backend.DTO.Response.ProjectResponseDTO;
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
    public List<ProjectResponseDTO> listProjects() {
        List<ProjectEntity> projects = projectRepository.findAll();
        List<ProjectResponseDTO> dtoList = new ArrayList<>();

        for (ProjectEntity p : projects) {
            ProjectResponseDTO dto = new ProjectResponseDTO();
            dto.setId(p.getId());
            dto.setName(p.getName());
            dto.setRepositoryUrl(p.getRepositoryUrl());
            dto.setProjectType(p.getProjectType());
            dto.setSonarProjectKey(p.getSonarProjectKey());
            dto.setCreatedAt(p.getCreatedAt());
            dto.setUpdatedAt(p.getUpdatedAt());
            dtoList.add(dto);
        }

        return dtoList;
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
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        return response;
    }

    // แก้ไข repository
    public RepositoryResponseDTO updateRepository(UUID id) {
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
            projectRepository.delete(entity);

            RepositoryResponseDTO response = new RepositoryResponseDTO();
            response.setMessage("Repository deleted successfully");
            return response;

        } catch (Exception e) {
            throw new RuntimeException("Server Error", e);
        }

    }

}
