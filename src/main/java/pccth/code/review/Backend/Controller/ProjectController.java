package pccth.code.review.Backend.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import pccth.code.review.Backend.DTO.Request.RepositoryDTO;
import pccth.code.review.Backend.Service.ProjectService;

@RestController
@RequestMapping("/repository")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/getMessage")
    public String getMessage() {
        return "Hello World";
    }

    @PostMapping("/new-repository")
    public void addRepository(@Valid @RequestBody RepositoryDTO repository) {
        projectService.addRepository(repository);
    }

}
