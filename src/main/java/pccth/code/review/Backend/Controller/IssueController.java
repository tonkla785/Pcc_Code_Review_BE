package pccth.code.review.Backend.Controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pccth.code.review.Backend.DTO.Request.CommentRequestDTO;
import pccth.code.review.Backend.DTO.Request.IssueUpdateRequestDTO;
import pccth.code.review.Backend.DTO.Response.CommentResponseDTO;
import pccth.code.review.Backend.DTO.Response.IssuesReponseDTO;
import pccth.code.review.Backend.Service.IssueService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class IssueController {

    private final IssueService issueService;

    public IssueController(IssueService issueService) {
        this.issueService = issueService;
    }

    @GetMapping("/issues")
    public ResponseEntity<List<IssuesReponseDTO>> getAllIssues() {
        return ResponseEntity.ok(issueService.getAllIssues());
    }

    @GetMapping("/issues/{id}")
    public ResponseEntity<IssuesReponseDTO> findIssueById(@PathVariable UUID id) {
        return ResponseEntity.ok(issueService.findIssueById(id));
    }

     @PostMapping("/issues/update")
     public ResponseEntity<IssuesReponseDTO> updateIssuePost(@Valid @RequestBody IssueUpdateRequestDTO req) {
         return ResponseEntity.ok(issueService.updateIssue(req));
     }
}