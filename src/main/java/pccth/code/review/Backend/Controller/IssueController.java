package pccth.code.review.Backend.Controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pccth.code.review.Backend.DTO.Request.CommentRequestDTO;
import pccth.code.review.Backend.DTO.Response.CommentResponseDTO;
import pccth.code.review.Backend.Service.IssueService;

import java.util.UUID;

@RestController
@RequestMapping("/api")
public class IssueController {

    private final IssueService issueService;

    public IssueController(IssueService issueService) {
        this.issueService = issueService;
    }

    @PostMapping("issues/{id}/comments")
    public ResponseEntity<CommentResponseDTO> addComment(@PathVariable UUID id, @Valid @RequestBody CommentRequestDTO request) {
        return ResponseEntity.ok(issueService.addComment(id, request));
    }
}