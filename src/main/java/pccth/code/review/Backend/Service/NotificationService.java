package pccth.code.review.Backend.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pccth.code.review.Backend.DTO.Request.NotificationRequestDTO;
import pccth.code.review.Backend.DTO.Response.NotificationResponseDTO;
import pccth.code.review.Backend.Entity.*;
import pccth.code.review.Backend.Repository.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ScanRepository scanRepository;
    private final IssueRepository issueRepository;
    private final CommentRepository commentRepository;

    public NotificationService(
            NotificationRepository notificationRepository,
            UserRepository userRepository,
            ProjectRepository projectRepository,
            ScanRepository scanRepository,
            IssueRepository issueRepository,
            CommentRepository commentRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.scanRepository = scanRepository;
        this.issueRepository = issueRepository;
        this.commentRepository = commentRepository;
    }

    public List<NotificationResponseDTO> getAllByUserId(UUID userId) {
        List<NotificationEntity> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return notifications.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public NotificationResponseDTO createNotification(NotificationRequestDTO request) {
        UserEntity user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        NotificationEntity notification = new NotificationEntity();
        notification.setUser(user);
        notification.setType(request.getType());
        notification.setTitle(request.getTitle());
        notification.setMessage(request.getMessage());
        notification.setIsRead(false);

        // Set related entities if provided
        if (request.getRelatedProjectId() != null) {
            projectRepository.findById(request.getRelatedProjectId())
                    .ifPresent(notification::setRelatedProject);
        }
        if (request.getRelatedScanId() != null) {
            scanRepository.findById(request.getRelatedScanId())
                    .ifPresent(notification::setRelatedScan);
        }
        if (request.getRelatedIssueId() != null) {
            issueRepository.findById(request.getRelatedIssueId())
                    .ifPresent(notification::setRelatedIssue);
        }
        if (request.getRelatedCommentId() != null) {
            commentRepository.findById(request.getRelatedCommentId())
                    .ifPresent(notification::setRelatedComment);
        }

        NotificationEntity saved = notificationRepository.save(notification);
        return mapToResponseDTO(saved);
    }

    @Transactional
    public void markAsRead(UUID notificationId) {
        notificationRepository.markAsRead(notificationId);
    }

    @Transactional
    public void markAllAsRead(UUID userId) {
        notificationRepository.markAllAsReadByUserId(userId);
    }

    private NotificationResponseDTO mapToResponseDTO(NotificationEntity entity) {
        NotificationResponseDTO dto = new NotificationResponseDTO();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUser().getId());
        dto.setType(entity.getType());
        dto.setTitle(entity.getTitle());
        dto.setMessage(entity.getMessage());
        dto.setIsRead(entity.getIsRead());
        dto.setCreatedAt(entity.getCreatedAt());

        if (entity.getRelatedProject() != null) {
            dto.setRelatedProjectId(entity.getRelatedProject().getId());
        }
        if (entity.getRelatedScan() != null) {
            dto.setRelatedScanId(entity.getRelatedScan().getId());
        }
        if (entity.getRelatedIssue() != null) {
            dto.setRelatedIssueId(entity.getRelatedIssue().getId());
        }
        if (entity.getRelatedComment() != null) {
            dto.setRelatedCommentId(entity.getRelatedComment().getId());
        }

        return dto;
    }
}
