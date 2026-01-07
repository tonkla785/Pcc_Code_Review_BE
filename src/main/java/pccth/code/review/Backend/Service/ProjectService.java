package pccth.code.review.Backend.Service;

import java.util.Date;
import java.util.UUID;

import org.springframework.stereotype.Service;

import pccth.code.review.Backend.DTO.Request.RepositoryDTO;
import pccth.code.review.Backend.Entity.ProjectEntity;
import pccth.code.review.Backend.EnumType.ProjectTypeEnum;
import pccth.code.review.Backend.Repository.ProjectRepository;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public void addRepository(RepositoryDTO repository) {

        ProjectEntity project = new ProjectEntity();
        project.setId(UUID.randomUUID());
        project.setName(repository.getName());
        project.setRepositoryUrl(repository.getUrl());
        project.setProjectType(ProjectTypeEnum.valueOf(repository.getType()));
        project.setSonarProjectKey(repository.getSonarKey());
        project.setCreatedAt(new Date());
        project.setUpdatedAt(new Date());

        projectRepository.save(project);
    }

}
