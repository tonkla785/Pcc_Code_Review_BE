package pccth.code.review.Backend.Controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pccth.code.review.Backend.DTO.Request.CommentRequestDTO;
import pccth.code.review.Backend.DTO.Response.CommentResponseDTO;
import pccth.code.review.Backend.Service.CommentService;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<CommentResponseDTO> addComment(
            @Valid @RequestBody CommentRequestDTO request
    ) {
        return ResponseEntity.ok(commentService.addComment(request));
    }
}
