package pccth.code.review.Backend.Controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pccth.code.review.Backend.DTO.Request.MarkdownReportRequestDTO;
import pccth.code.review.Backend.DTO.Request.ReportGenerateRequestDTO;
import pccth.code.review.Backend.DTO.Response.ProjectSummaryDTO;
import pccth.code.review.Backend.DTO.Response.ReportGenerateResponseDTO;
import pccth.code.review.Backend.Service.ReportService;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping("/generate")
    public ResponseEntity<ReportGenerateResponseDTO> generate(@Valid @RequestBody ReportGenerateRequestDTO request) {
        return ResponseEntity.ok(reportService.generate(request));
    }

    @PostMapping(value = "/markdown", produces = "text/markdown;charset=UTF-8")
    public ResponseEntity<String> generateMarkdown(@Valid @RequestBody MarkdownReportRequestDTO request) {
        return ResponseEntity.ok(reportService.generateMarkdown(request));
    }

    @GetMapping("/projects")
    public ResponseEntity<List<ProjectSummaryDTO>> listProjects() {
        return ResponseEntity.ok(reportService.listProjects());
    }
}

