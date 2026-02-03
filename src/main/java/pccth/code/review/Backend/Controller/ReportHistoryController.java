package pccth.code.review.Backend.Controller;

import org.springframework.http.ResponseEntity;
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

    @GetMapping("/{userId}")
    public ResponseEntity<List<ReportHistoryResponseDTO>> getAllReportHistory(@PathVariable String userId) {
        UUID userIdUUID = UUID.fromString(userId);
        List<ReportHistoryResponseDTO> reports = reportHistoryService.getAllByUserId(userIdUUID);
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/search/{userId}")
    public ResponseEntity<List<ReportHistoryResponseDTO>> searchByProjectName(
            @PathVariable String userId,
            @RequestParam String keyword) {
        UUID userIdUUID = UUID.fromString(userId);
        List<ReportHistoryResponseDTO> reports = reportHistoryService.searchByProjectName(userIdUUID, keyword);
        return ResponseEntity.ok(reports);
    }

    @PostMapping("/create/{userId}")
    public ResponseEntity<ReportHistoryResponseDTO> createReportHistory(
            @PathVariable String userId,
            @Valid @RequestBody ReportHistoryRequestDTO request) {
        UUID userIdUUID = UUID.fromString(userId);
        ReportHistoryResponseDTO report = reportHistoryService.createReportHistory(userIdUUID, request);
        return ResponseEntity.ok(report);
    }
}
