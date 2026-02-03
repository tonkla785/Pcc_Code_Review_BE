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
<<<<<<< HEAD
    public ResponseEntity<List<ReportHistoryResponseDTO>> getAllReportHistory(@PathVariable String userId) {
        UUID userIdUUID = UUID.fromString(userId);
        List<ReportHistoryResponseDTO> reports = reportHistoryService.getAllByUserId(userIdUUID);
=======
    public ResponseEntity<List<ReportHistoryResponseDTO>> getAllReportHistory(@PathVariable String id) {
        UUID userId = UUID.fromString(id);
        List<ReportHistoryResponseDTO> reports = reportHistoryService.getAllByUserId(userId);
>>>>>>> main
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/search/{userId}")
    public ResponseEntity<List<ReportHistoryResponseDTO>> searchByProjectName(
<<<<<<< HEAD
            @PathVariable String userId,
            @RequestParam String keyword) {
        UUID userIdUUID = UUID.fromString(userId);
        List<ReportHistoryResponseDTO> reports = reportHistoryService.searchByProjectName(userIdUUID, keyword);
=======
            @PathVariable String id,
            @RequestParam String keyword) {
        UUID userId = UUID.fromString(id);
        List<ReportHistoryResponseDTO> reports = reportHistoryService.searchByProjectName(userId, keyword);
>>>>>>> main
        return ResponseEntity.ok(reports);
    }

    @PostMapping("/create/{userId}")
    public ResponseEntity<ReportHistoryResponseDTO> createReportHistory(
<<<<<<< HEAD
            @PathVariable String userId,
            @Valid @RequestBody ReportHistoryRequestDTO request) {
        UUID userIdUUID = UUID.fromString(userId);
        ReportHistoryResponseDTO report = reportHistoryService.createReportHistory(userIdUUID, request);
=======
            @PathVariable String id,
            @Valid @RequestBody ReportHistoryRequestDTO request) {
        UUID userId = UUID.fromString(id);
        ReportHistoryResponseDTO report = reportHistoryService.createReportHistory(userId, request);
>>>>>>> main
        return ResponseEntity.ok(report);
    }
}
