package pccth.code.review.Backend.Service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pccth.code.review.Backend.DTO.Request.CommentRequestDTO;
import pccth.code.review.Backend.DTO.Request.NotificationRequestDTO;
import pccth.code.review.Backend.DTO.Response.CommentResponseDTO;
import pccth.code.review.Backend.DTO.Response.NotificationResponseDTO;
import pccth.code.review.Backend.DTO.Response.UserResponseDTO;
import pccth.code.review.Backend.Entity.CommentEntity;
import pccth.code.review.Backend.Entity.IssueEntity;
import pccth.code.review.Backend.Entity.UserEntity;
import pccth.code.review.Backend.Repository.CommentRepository;
import pccth.code.review.Backend.Repository.IssueRepository;
import pccth.code.review.Backend.Repository.UserRepository;

import java.util.Date;
import java.util.UUID;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final IssueRepository issueRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final WebSocketNotificationService webSocketNotificationService;

    public CommentService(
            CommentRepository commentRepository,
            IssueRepository issueRepository,
            UserRepository userRepository,
            NotificationService notificationService,
            WebSocketNotificationService webSocketNotificationService) {
        this.commentRepository = commentRepository;
        this.issueRepository = issueRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.webSocketNotificationService = webSocketNotificationService;
    }

    @Transactional
    public CommentResponseDTO addComment(CommentRequestDTO request) {

        IssueEntity issue = issueRepository.findById(request.getIssueId())
                .orElseThrow(() -> new RuntimeException("Issue not found"));

        UserEntity user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        CommentEntity comment = new CommentEntity();
        comment.setIssue(issue);
        comment.setUser(user);
        comment.setComment(request.getComment());
        comment.setCreatedAt(new Date());

        // Handle parent comment for replies
        CommentEntity parentComment = null;
        if (request.getParentCommentId() != null) {
            parentComment = commentRepository.findById(request.getParentCommentId())
                    .orElseThrow(() -> new RuntimeException("Parent comment not found"));
            comment.setParentComment(parentComment);
        }

        CommentEntity saved = commentRepository.save(comment);

        // Send real-time notification
        sendCommentNotification(saved, issue, user, parentComment);

        return mapToCommentResponseDTO(saved);
    }

    /**
     * ส่ง notification เมื่อมี comment ใหม่
     */
    private void sendCommentNotification(CommentEntity comment, IssueEntity issue, UserEntity commenter,
            CommentEntity parentComment) {
        UUID targetUserId = null;
        String title = "";
        String message = "";

        if (parentComment != null) {
            // Reply to a comment - notify the parent comment owner
            targetUserId = parentComment.getUser().getId();
            title = "New Reply";
            message = commenter.getUsername() + " replied to your comment on issue: " + issue.getMessage();
        } else if (issue.getAssignedTo() != null) {
            // New comment on issue - notify the assigned user
            targetUserId = issue.getAssignedTo().getId();
            title = "New Comment";
            message = commenter.getUsername() + " commented on issue: " + issue.getMessage();
        }

        // Don't notify if the commenter is the target user
        if (targetUserId != null && !targetUserId.equals(commenter.getId())) {
            NotificationRequestDTO notificationRequest = new NotificationRequestDTO();
            notificationRequest.setUserId(targetUserId);
            notificationRequest.setType("Issues");
            notificationRequest.setTitle(title);
            notificationRequest.setMessage(message);
            notificationRequest.setRelatedIssueId(issue.getId());
            notificationRequest.setRelatedCommentId(comment.getId());

            // Save to database
            NotificationResponseDTO notification = notificationService.createNotification(notificationRequest);

            // Send via WebSocket for real-time
            webSocketNotificationService.sendNotificationToUser(targetUserId, notification);
        }
    }

    public CommentResponseDTO mapToCommentResponseDTO(CommentEntity comment) {
        CommentResponseDTO dto = new CommentResponseDTO();
        UserEntity user = comment.getUser();
        UserResponseDTO userDTO = new UserResponseDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setPhone(user.getPhone());
        userDTO.setRole(user.getRole());
        userDTO.setCreateAt(comment.getCreatedAt());
        dto.setId(comment.getId());
        dto.setIssue(comment.getIssue().getId());
        dto.setUser(userDTO);
        dto.setComment(comment.getComment());
        dto.setCreatedAt(comment.getCreatedAt());

        // Set parent comment ID if exists
        if (comment.getParentComment() != null) {
            dto.setParentCommentId(comment.getParentComment().getId());
        }

        return dto;
    }
}
