package pccth.code.review.Backend.Controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pccth.code.review.Backend.DTO.Request.ScanRequestsDTO;
import pccth.code.review.Backend.DTO.Response.IssueStatusCountDTO;
import pccth.code.review.Backend.DTO.Response.IssuesReponseDTO;
import pccth.code.review.Backend.DTO.Response.ProjectResponseDTO;
import pccth.code.review.Backend.DTO.Response.ScanResponseDTO;
import pccth.code.review.Backend.Service.ScanService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@PreAuthorize("isAuthenticated()")
public class ScanController {
    @Autowired
    private ScanService scanService;


    @GetMapping("/{projectId}/history")
    public ResponseEntity<List<ScanResponseDTO>> getScansByProjectId(@PathVariable UUID projectId) {
        List<ScanResponseDTO> scans = scanService.getScansHistory(projectId);
        return ResponseEntity.ok(scans);
    }
    @GetMapping("/test-error")
    public String testError() throws Exception {
        throw new Exception("Boom!");
    }
    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectResponseDTO> getProject(@PathVariable UUID projectId) {
        ProjectResponseDTO projectResponseDTO = scanService.getProject(projectId);
        return ResponseEntity.ok(projectResponseDTO);
    }
    @GetMapping("/{projectId}/trends")
    public ResponseEntity<List<IssueStatusCountDTO>> getProjectIssue(@PathVariable UUID projectId) {
        List<IssueStatusCountDTO> issueStatusCountDTO  = scanService.getScansIssue(projectId);
        return ResponseEntity.ok(issueStatusCountDTO);
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
    @GetMapping("/scans/{scanId}/log")
    public ResponseEntity <ScanResponseDTO> getScanLog(@PathVariable UUID scanId) {
        ScanResponseDTO scanResponseDTO = scanService.getScansLog(scanId);
        return ResponseEntity.ok(scanResponseDTO);
    }
    @PostMapping("/scans")
    public ResponseEntity<ScanResponseDTO> createScan(@Valid
                                                      @RequestBody ScanRequestsDTO request) {

        return ResponseEntity.ok(scanService.SaveScan(request));
    }




}