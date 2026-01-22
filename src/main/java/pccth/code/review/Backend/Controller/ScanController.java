package pccth.code.review.Backend.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pccth.code.review.Backend.DTO.Response.ProjectResponseDTO;
import pccth.code.review.Backend.DTO.Response.ScanResponseDTO;
import pccth.code.review.Backend.Service.ScanService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class ScanController {
    @Autowired
    private ScanService scanService;

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectResponseDTO> getProject(@PathVariable UUID projectId) {
        ProjectResponseDTO projectResponseDTO = scanService.getProject(projectId);
        return ResponseEntity.ok(projectResponseDTO);
    }

    @GetMapping("/scans/{scanId}")
    public ResponseEntity<ScanResponseDTO> getScanById(@PathVariable UUID scanId) {
        ScanResponseDTO scanResponseDTO = scanService.getScansById(scanId);
        return ResponseEntity.ok(scanResponseDTO);
    }

    @GetMapping("/scans")
    public ResponseEntity<List<ScanResponseDTO>> getScanAll() {
        List<ScanResponseDTO> scanResponseDTO = scanService.getScansAll();
        return ResponseEntity.ok(scanResponseDTO);
    }
}