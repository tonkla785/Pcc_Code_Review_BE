package pccth.code.review.Backend.Service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pccth.code.review.Backend.DTO.Request.SonarQubeConfigRequestDTO;
import pccth.code.review.Backend.DTO.Response.SonarQubeConfigResponseDTO;
import pccth.code.review.Backend.Entity.SonarQubeConfigEntity;
import pccth.code.review.Backend.Entity.UserEntity;
import pccth.code.review.Backend.Repository.SonarQubeConfigRepository;
import pccth.code.review.Backend.Repository.UserRepository;

import java.util.Date;
import java.util.UUID;

@Service
public class SonarQubeConfigService {

    private final SonarQubeConfigRepository sonarQubeConfigRepository;
    private final UserRepository userRepository;

    public SonarQubeConfigService(
            SonarQubeConfigRepository sonarQubeConfigRepository,
            UserRepository userRepository) {
        this.sonarQubeConfigRepository = sonarQubeConfigRepository;
        this.userRepository = userRepository;
    }

    public SonarQubeConfigResponseDTO getByUserId(UUID userId) {
        SonarQubeConfigEntity config = sonarQubeConfigRepository.findByUserId(userId)
                .orElse(null);

        if (config == null) {
            return createDefaultConfig(userId);
        }

        return mapToResponseDTO(config);
    }

    @Transactional
    public SonarQubeConfigResponseDTO createDefaultConfig(UUID userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        SonarQubeConfigEntity config = new SonarQubeConfigEntity();
        config.setUser(user);
        // Set defaults
        config.setAngularRunNpm(false);
        config.setAngularCoverage(false);
        config.setAngularTsFiles(false);
        config.setSpringRunTests(false);
        config.setSpringJacoco(false);
        config.setQgFailOnError(false);
        config.setQgCoverageThreshold(0);
        config.setQgMaxBugs(0);
        config.setQgMaxVulnerabilities(0);
        config.setQgMaxCodeSmells(0);
        config.setCreatedAt(new Date());
        config.setUpdatedAt(new Date());
        SonarQubeConfigEntity saved = sonarQubeConfigRepository.save(config);
        return mapToResponseDTO(saved);
    }

    @Transactional
    public SonarQubeConfigResponseDTO updateConfig(UUID userId, SonarQubeConfigRequestDTO request) {
        SonarQubeConfigEntity config = sonarQubeConfigRepository.findByUserId(userId)
                .orElseGet(() -> {
                    UserEntity user = userRepository.findById(userId)
                            .orElseThrow(() -> new RuntimeException("User not found"));
                    SonarQubeConfigEntity newConfig = new SonarQubeConfigEntity();
                    newConfig.setUser(user);
                    return newConfig;
                });

        // Update fields if not null
        if (request.getServerUrl() != null)
            config.setServerUrl(request.getServerUrl());
        if (request.getAuthToken() != null)
            config.setAuthToken(request.getAuthToken());
        if (request.getOrganization() != null)
            config.setOrganization(request.getOrganization());

        // Angular settings
        if (request.getAngularRunNpm() != null)
            config.setAngularRunNpm(request.getAngularRunNpm());
        if (request.getAngularCoverage() != null)
            config.setAngularCoverage(request.getAngularCoverage());
        if (request.getAngularTsFiles() != null)
            config.setAngularTsFiles(request.getAngularTsFiles());
        if (request.getAngularExclusions() != null)
            config.setAngularExclusions(request.getAngularExclusions());

        // Spring settings
        if (request.getSpringRunTests() != null)
            config.setSpringRunTests(request.getSpringRunTests());
        if (request.getSpringJacoco() != null)
            config.setSpringJacoco(request.getSpringJacoco());
        if (request.getSpringBuildTool() != null)
            config.setSpringBuildTool(request.getSpringBuildTool());
        if (request.getSpringJdkVersion() != null)
            config.setSpringJdkVersion(request.getSpringJdkVersion());

        // Quality gates
        if (request.getQgFailOnError() != null)
            config.setQgFailOnError(request.getQgFailOnError());
        if (request.getQgCoverageThreshold() != null)
            config.setQgCoverageThreshold(request.getQgCoverageThreshold());
        if (request.getQgMaxBugs() != null)
            config.setQgMaxBugs(request.getQgMaxBugs());
        if (request.getQgMaxVulnerabilities() != null)
            config.setQgMaxVulnerabilities(request.getQgMaxVulnerabilities());
        if (request.getQgMaxCodeSmells() != null)
            config.setQgMaxCodeSmells(request.getQgMaxCodeSmells());

        // Set updatedAt for update
        config.setUpdatedAt(new Date());
        if (config.getCreatedAt() == null) {
            config.setCreatedAt(new Date());
        }

        SonarQubeConfigEntity saved = sonarQubeConfigRepository.save(config);
        return mapToResponseDTO(saved);
    }

    private SonarQubeConfigResponseDTO mapToResponseDTO(SonarQubeConfigEntity entity) {
        SonarQubeConfigResponseDTO dto = new SonarQubeConfigResponseDTO();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUser().getId());
        dto.setServerUrl(entity.getServerUrl());
        dto.setOrganization(entity.getOrganization());
        dto.setAuthToken(entity.getAuthToken());

        dto.setAngularRunNpm(entity.getAngularRunNpm());
        dto.setAngularCoverage(entity.getAngularCoverage());
        dto.setAngularTsFiles(entity.getAngularTsFiles());
        dto.setAngularExclusions(entity.getAngularExclusions());

        dto.setSpringRunTests(entity.getSpringRunTests());
        dto.setSpringJacoco(entity.getSpringJacoco());
        dto.setSpringBuildTool(entity.getSpringBuildTool());
        dto.setSpringJdkVersion(entity.getSpringJdkVersion());

        dto.setQgFailOnError(entity.getQgFailOnError());
        dto.setQgCoverageThreshold(entity.getQgCoverageThreshold());
        dto.setQgMaxBugs(entity.getQgMaxBugs());
        dto.setQgMaxVulnerabilities(entity.getQgMaxVulnerabilities());
        dto.setQgMaxCodeSmells(entity.getQgMaxCodeSmells());

        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
