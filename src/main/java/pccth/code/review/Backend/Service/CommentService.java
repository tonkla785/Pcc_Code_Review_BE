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
     * แจ้งเตือนทุกคนที่เคย comment บน issue นี้ (ยกเว้นคน comment นี้)
     */
    private void sendCommentNotification(CommentEntity comment, IssueEntity issue, UserEntity commenter,
            CommentEntity parentComment) {

        String title;
        String message;

        if (parentComment != null) {
            // Reply to a comment
            title = "💬 New Reply";
            message = "@" + commenter.getUsername() + " replied to a comment on issue: "
                    + truncateMessage(issue.getMessage(), 50);
        } else {
            // New comment on issue
            title = "💬 New Comment";
            message = "@" + commenter.getUsername() + " commented on issue: " + truncateMessage(issue.getMessage(), 50);
        }

        // Get all users who have commented on this issue
        List<UUID> usersToNotify = commentRepository.findDistinctUserIdsByIssueId(issue.getId());

        // Also add assigned user if exists
        if (issue.getAssignedTo() != null && !usersToNotify.contains(issue.getAssignedTo().getId())) {
            usersToNotify.add(issue.getAssignedTo().getId());
        }

        // Send notification to each user (except the commenter)
        for (UUID targetUserId : usersToNotify) {
            if (!targetUserId.equals(commenter.getId())) {
                NotificationRequestDTO notificationRequest = new NotificationRequestDTO();
                notificationRequest.setUserId(targetUserId);
                notificationRequest.setType("System");
                notificationRequest.setTitle(title);
                notificationRequest.setMessage(message);
                notificationRequest.setRelatedIssueId(issue.getId());
                notificationRequest.setRelatedCommentId(comment.getId());

                // Save to database and send via WebSocket (NotificationService handles
                // WebSocket now)
                notificationService.createNotification(notificationRequest);
            }
        }
    }

    private String truncateMessage(String message, int maxLength) {
        if (message == null)
            return "";
        return message.length() > maxLength ? message.substring(0, maxLength) + "..." : message;
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
