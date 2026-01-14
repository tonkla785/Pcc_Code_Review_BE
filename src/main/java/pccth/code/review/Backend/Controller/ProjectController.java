package pccth.code.review.Backend.Controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import pccth.code.review.Backend.DTO.Request.RepositoryDTO;
import pccth.code.review.Backend.DTO.Response.ProjectResponseDTO;
import pccth.code.review.Backend.DTO.Response.RepositoryResponseDTO;
import pccth.code.review.Backend.Entity.ProjectEntity;
import pccth.code.review.Backend.Service.ProjectService;

@RestController
@RequestMapping("/repository")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    // เพิ่ม repository
    @PostMapping("/new-repository")
    public ResponseEntity<RepositoryResponseDTO> addRepository(@Valid @RequestBody RepositoryDTO repository) {
        RepositoryResponseDTO response = projectService.addRepository(repository);
        return ResponseEntity.status(201).body(response); // 201 สำหรับการสร้างใหม่
    }

    // ดึง repository ทั้งหมด
    @GetMapping("/all-repository")
    public List<ProjectResponseDTO> listProjects() {
        return projectService.listProjects();
    }

    // ดึง repository เฉพาะตัว id
    @GetMapping("/search-repositories/{id}")
    public ProjectResponseDTO getRepository(@PathVariable UUID id) {
        return projectService.searchRepository(id);
    }

    // แก้ไข repository เฉพาะตัว id
    @PutMapping("/update-repository/{id}")
    public ResponseEntity<RepositoryResponseDTO> updateRepository(@PathVariable UUID id,
                                                                  @Valid @RequestBody RepositoryDTO repository) {
    public ResponseEntity<RepositoryResponseDTO> updateRepository(@PathVariable UUID id,
                                                                  @Valid @RequestBody RepositoryDTO repository) {
        RepositoryResponseDTO response = projectService.updateRepository(id, repository);
        return ResponseEntity.status(200).body(response);
    }

    // ลบ repository เฉพาะตัว id
    @DeleteMapping("/delete-repository/{id}")
    public ResponseEntity<RepositoryResponseDTO> deleteRepository(@PathVariable UUID id) {
        RepositoryResponseDTO response = projectService.deleteRepository(id);
        return ResponseEntity.status(200).body(response);
    }
}