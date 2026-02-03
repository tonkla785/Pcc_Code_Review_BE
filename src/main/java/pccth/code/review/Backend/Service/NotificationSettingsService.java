package pccth.code.review.Backend.Service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pccth.code.review.Backend.DTO.Request.NotificationSettingsRequestDTO;
import pccth.code.review.Backend.DTO.Response.NotificationSettingsResponseDTO;
import pccth.code.review.Backend.Entity.NotificationSettingsEntity;
import pccth.code.review.Backend.Entity.UserEntity;
import pccth.code.review.Backend.Repository.NotificationSettingsRepository;
import pccth.code.review.Backend.Repository.UserRepository;

import java.util.Date;
import java.util.UUID;

@Service
public class NotificationSettingsService {

    private final NotificationSettingsRepository notificationSettingsRepository;
    private final UserRepository userRepository;

    public NotificationSettingsService(
            NotificationSettingsRepository notificationSettingsRepository,
            UserRepository userRepository) {
        this.notificationSettingsRepository = notificationSettingsRepository;
        this.userRepository = userRepository;
    }

    public NotificationSettingsResponseDTO getByUserId(UUID userId) {
        NotificationSettingsEntity settings = notificationSettingsRepository.findByUserId(userId)
                .orElse(null);

        if (settings == null) {
            // Create default settings if not exists
            return createDefaultSettings(userId);
        }

        return mapToResponseDTO(settings);
    }

    @Transactional
    public NotificationSettingsResponseDTO createDefaultSettings(UUID userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        NotificationSettingsEntity settings = new NotificationSettingsEntity();
        settings.setUser(user);
        settings.setScansEnabled(true);
        settings.setIssuesEnabled(true);
        settings.setSystemEnabled(true);
        settings.setReportsEnabled(false);
        settings.setCreatedAt(new Date());
        settings.setUpdatedAt(new Date());

        NotificationSettingsEntity saved = notificationSettingsRepository.save(settings);
        return mapToResponseDTO(saved);
    }

    @Transactional
    public NotificationSettingsResponseDTO updateSettings(UUID userId, NotificationSettingsRequestDTO request) {
        NotificationSettingsEntity settings = notificationSettingsRepository.findByUserId(userId)
                .orElseGet(() -> {
                    UserEntity user = userRepository.findById(userId)
                            .orElseThrow(() -> new RuntimeException("User not found"));
                    NotificationSettingsEntity newSettings = new NotificationSettingsEntity();
                    newSettings.setUser(user);
                    return newSettings;
                });

        if (request.getScansEnabled() != null) {
            settings.setScansEnabled(request.getScansEnabled());
        }
        if (request.getIssuesEnabled() != null) {
            settings.setIssuesEnabled(request.getIssuesEnabled());
        }
        if (request.getSystemEnabled() != null) {
            settings.setSystemEnabled(request.getSystemEnabled());
        }
        if (request.getReportsEnabled() != null) {
            settings.setReportsEnabled(request.getReportsEnabled());
        }

        // Set updatedAt for update
        settings.setUpdatedAt(new Date());
        if (settings.getCreatedAt() == null) {
            settings.setCreatedAt(new Date());
        }

        NotificationSettingsEntity saved = notificationSettingsRepository.save(settings);
        return mapToResponseDTO(saved);
    }

    private NotificationSettingsResponseDTO mapToResponseDTO(NotificationSettingsEntity entity) {
        NotificationSettingsResponseDTO dto = new NotificationSettingsResponseDTO();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUser().getId());
        dto.setScansEnabled(entity.getScansEnabled());
        dto.setIssuesEnabled(entity.getIssuesEnabled());
        dto.setSystemEnabled(entity.getSystemEnabled());
        dto.setReportsEnabled(entity.getReportsEnabled());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
