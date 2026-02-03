package pccth.code.review.Backend.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pccth.code.review.Backend.DTO.Request.ReportHistoryRequestDTO;
import pccth.code.review.Backend.DTO.Response.ReportHistoryResponseDTO;
import pccth.code.review.Backend.Service.ReportHistoryService;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/report-history")
public class ReportHistoryController {

    private final ReportHistoryService reportHistoryService;

    public ReportHistoryController(ReportHistoryService reportHistoryService) {
        this.reportHistoryService = reportHistoryService;
    }

    @GetMapping
    public ResponseEntity<List<ReportHistoryResponseDTO>> getAllReportHistory(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        List<ReportHistoryResponseDTO> reports = reportHistoryService.getAllByUserId(userId);
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ReportHistoryResponseDTO>> searchByProjectName(
            Authentication authentication,
            @RequestParam String keyword) {
        UUID userId = UUID.fromString(authentication.getName());
        List<ReportHistoryResponseDTO> reports = reportHistoryService.searchByProjectName(userId, keyword);
        return ResponseEntity.ok(reports);
    }

    @PostMapping
    public ResponseEntity<ReportHistoryResponseDTO> createReportHistory(
            Authentication authentication,
            @Valid @RequestBody ReportHistoryRequestDTO request) {
        UUID userId = UUID.fromString(authentication.getName());
        ReportHistoryResponseDTO report = reportHistoryService.createReportHistory(userId, request);
        return ResponseEntity.ok(report);
    }
}
