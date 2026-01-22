package pccth.code.review.Backend.Service;

import org.springframework.stereotype.Service;
import pccth.code.review.Backend.DTO.Request.N8NRequestDTO;
import pccth.code.review.Backend.EnumType.ProjectTypeEnum;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.UUID;

@Service
public class SonarScanService {

    public Map<String, Object> execute(N8NRequestDTO req) {
        try {
            UUID scanId = req.getScanId();
            String projectKey = req.getSonarProjectKey();
            ProjectTypeEnum type = req.getProjectType();

            String workDir = "/scan-workspace/" + scanId;

            String sonarHost = System.getenv("SONAR_HOST_URL");
            String sonarToken = System.getenv("SONAR_TOKEN");

            if (sonarHost == null || sonarToken == null) {
                throw new IllegalStateException("SONAR_HOST_URL or SONAR_TOKEN not set");
            }

            if (type == ProjectTypeEnum.ANGULAR) {
                ensureNodeAvailable();
            }

            String command = buildCommand(type, workDir, projectKey, sonarHost, sonarToken);

            ProcessBuilder pb = new ProcessBuilder("sh", "-c", command);
            pb.directory(new File(workDir));
            pb.inheritIO();

            pb.start();

            String ceTaskId = waitForCeTaskId(workDir, 30);

            return Map.of(
                    "scanId", scanId,
                    "projectType", type.name(),
                    "status", "SONAR_TASK_SUBMITTED",
                    "ceTaskId", ceTaskId
            );

        } catch (Exception e) {
            throw new RuntimeException("Sonar scan execution error", e);
        }
    }

    private String buildCommand(
            ProjectTypeEnum type,
            String workDir,
            String projectKey,
            String sonarHost,
            String sonarToken
    ) {

        return switch (type) {

            case SPRING_BOOT -> {

                boolean isGradle = new File(workDir + "/gradlew").exists()
                        || new File(workDir + "/build.gradle").exists()
                        || new File(workDir + "/build.gradle.kts").exists();

                String buildCmd = isGradle
                        ? """
                        chmod +x gradlew || true
                        ./gradlew build -x test
                        """
                        : """
                        if [ -f mvnw ]; then
                          chmod +x mvnw
                          ./mvnw -B -DskipTests compile
                        else
                          mvn -B -DskipTests compile
                        fi
                        """;

                yield """
                        set -e
                        %s
                        sonar-scanner \
                          -Dsonar.projectKey=%s \
                          -Dsonar.sources=src/main/java \
                          -Dsonar.java.binaries=%s \
                          -Dsonar.host.url=%s \
                          -Dsonar.login=%s
                        """.formatted(
                        buildCmd,
                        projectKey,
                        isGradle ? "build/classes/java/main" : "target/classes",
                        sonarHost,
                        sonarToken
                );
            }

            case ANGULAR -> """
                    set -e
                    sonar-scanner \
                      -Dsonar.projectKey=%s \
                      -Dsonar.sources=src \
                      -Dsonar.exclusions=**/node_modules/**,**/*.spec.ts \
                      -Dsonar.tests=src \
                      -Dsonar.test.inclusions=**/*.spec.ts \
                      -Dsonar.host.url=%s \
                      -Dsonar.login=%s
                    """.formatted(projectKey, sonarHost, sonarToken);

            default -> """
                    set -e
                    sonar-scanner \
                      -Dsonar.projectKey=%s \
                      -Dsonar.sources=. \
                      -Dsonar.host.url=%s \
                      -Dsonar.login=%s
                    """.formatted(projectKey, sonarHost, sonarToken);
        };
    }

    private String waitForCeTaskId(String workDir, int timeoutSeconds)
            throws InterruptedException, IOException {

        File report = new File(workDir + "/.scannerwork/report-task.txt");

        int waited = 0;
        while (!report.exists()) {
            if (waited++ >= timeoutSeconds) {
                throw new IllegalStateException("Timeout waiting for report-task.txt");
            }
            Thread.sleep(1000);
        }

        for (String line : Files.readAllLines(report.toPath())) {
            if (line.startsWith("ceTaskId=")) {
                return line.substring("ceTaskId=".length()).trim();
            }
        }

        throw new IllegalStateException("ceTaskId not found in report-task.txt");
    }

    private void ensureNodeAvailable() {
        try {
            Process p = new ProcessBuilder("node", "-v").start();
            if (p.waitFor() != 0) {
                throw new IllegalStateException("Node.js not available");
            }
        } catch (Exception e) {
            throw new IllegalStateException("Node.js is required for frontend scan", e);
        }
    }
}
