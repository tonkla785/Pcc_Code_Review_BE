package pccth.code.review.Backend.Service;

import org.springframework.stereotype.Service;
import pccth.code.review.Backend.DTO.Request.N8NRequestDTO;
import pccth.code.review.Backend.EnumType.DetectedProjectType;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@Service
public class SonarScanService {

    private static final String SCANNER_CONTAINER = "pccth-sonar-scanner";
    private static final Path WORKSPACE_BASE = Paths.get("/scan-workspace");

    public Map<String, Object> execute(N8NRequestDTO req) throws Exception {

        UUID scanId = req.getScanId();
        String projectKey = req.getSonarProjectKey();

        DetectedProjectType type = switch (req.getProjectType()) {
            case SPRING_BOOT -> DetectedProjectType.SPRING_BOOT;
            case ANGULAR -> DetectedProjectType.ANGULAR;
            default -> DetectedProjectType.GENERIC;
        };

        Path workspace = WORKSPACE_BASE.resolve(scanId.toString());

        String sonarArgs = buildSonarArgs(type, workspace, projectKey);

        String command =
                "cd /workspace/" + scanId +
                        " && sonar-scanner " + sonarArgs;

        ProcessBuilder pb = new ProcessBuilder(
                "docker", "exec",
                SCANNER_CONTAINER,
                "sh", "-c",
                command
        );

        pb.inheritIO();
        int exitCode = pb.start().waitFor();

        if (exitCode != 0) {
            throw new RuntimeException("Sonar scan failed");
        }

        return Map.of(
                "scanId", scanId,
                "status", "SONAR_SCAN_STARTED"
        );
    }

    private String buildSonarArgs(
            DetectedProjectType type,
            Path workspace,
            String projectKey
    ) {

        return switch (type) {
            case SPRING_BOOT -> String.join(" ",
                    "-Dsonar.projectKey=" + projectKey,
                    "-Dsonar.sources=src/main/java"
            );

            case ANGULAR -> {
                String tsconfigPath = resolveTsConfig(workspace);

                yield String.join(" ",
                        "-Dsonar.projectKey=" + projectKey,
                        "-Dsonar.sources=src",
                        "-Dsonar.exclusions=**/node_modules/**,**/*.spec.ts",
                        tsconfigPath != null
                                ? "-Dsonar.typescript.tsconfigPath=" + tsconfigPath
                                : ""
                );
            }

            default -> String.join(" ",
                    "-Dsonar.projectKey=" + projectKey,
                    "-Dsonar.sources=."
            );
        };
    }

    private String resolveTsConfig(Path workspace) {

        String[] candidates = {
                "tsconfig.app.json",
                "tsconfig.json",
                "tsconfig.base.json"
        };

        for (String file : candidates) {
            Path path = workspace.resolve(file);
            if (Files.exists(path)) {
                return file;
            }
        }

        try {
            return Files.list(workspace)
                    .filter(p -> p.getFileName().toString().startsWith("tsconfig"))
                    .filter(p -> p.getFileName().toString().endsWith(".jsonc"))
                    .findFirst()
                    .map(p -> p.getFileName().toString())
                    .orElse(null);
        } catch (Exception e) {
            return null;
        }
    }
}
