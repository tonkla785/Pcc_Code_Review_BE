package pccth.code.review.Backend.Service;

import org.springframework.stereotype.Service;
import pccth.code.review.Backend.DTO.Request.N8NRequestDTO;
import pccth.code.review.Backend.EnumType.ProjectTypeEnum;

import java.io.File;
import java.util.Map;
import java.util.UUID;

@Service
public class SonarScanService {

    public Map<String, Object> execute(N8NRequestDTO req) throws Exception {

        UUID scanId = req.getScanId();
        String projectKey = req.getSonarProjectKey();
        ProjectTypeEnum type = req.getProjectType();

        String workDir = "/scan-workspace/" + scanId;

        String sonarHost = System.getenv("SONAR_HOST_URL");
        String sonarToken = System.getenv("SONAR_TOKEN");

        if (sonarHost == null || sonarToken == null) {
            throw new IllegalStateException("SONAR_HOST_URL or SONAR_TOKEN not set");
        }

        String command = switch (type) {

            case SPRING_BOOT -> {

                boolean isGradle = new File(workDir + "/gradlew").exists()
                        || new File(workDir + "/build.gradle").exists()
                        || new File(workDir + "/build.gradle.kts").exists();

                boolean hasJacocoMaven = new File(
                        workDir + "/target/site/jacoco/jacoco.xml"
                ).exists();

                boolean hasJacocoGradle = new File(
                        workDir + "/build/reports/jacoco/test/jacocoTestReport.xml"
                ).exists();

                String coverageArg = "";
                if (hasJacocoMaven) {
                    coverageArg = "-Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml";
                } else if (hasJacocoGradle) {
                    coverageArg = "-Dsonar.coverage.jacoco.xmlReportPaths=build/reports/jacoco/test/jacocoTestReport.xml";
                }

                String buildCommand = isGradle
                        ? """
                        echo "=== BUILD SPRING BOOT (GRADLE) ==="
                        chmod +x gradlew || true
                        ./gradlew build -x test
                        """
                        : """
                        echo "=== BUILD SPRING BOOT (MAVEN) ==="
                        if [ -f mvnw ]; then
                          chmod +x mvnw
                          ./mvnw -B -DskipTests compile
                        else
                          mvn -B -DskipTests compile
                        fi
                        """;

                yield """
                        set -e
                        cd %1$s
                        
                        %3$s
                        
                        echo "=== RUN SONAR (SPRING BOOT) ==="
                        sonar-scanner \
                          -Dsonar.projectKey=%2$s \
                          -Dsonar.sources=src/main/java \
                          -Dsonar.java.binaries=%4$s \
                          %5$s \
                          -Dsonar.host.url=%6$s \
                          -Dsonar.login=%7$s
                        """.formatted(
                        workDir,
                        projectKey,
                        buildCommand,
                        isGradle ? "build/classes/java/main" : "target/classes",
                        coverageArg,
                        sonarHost,
                        sonarToken
                );
            }

            case ANGULAR -> {
                String tsconfig = resolveTsConfig(workDir);

                boolean hasCoverage = new File(
                        workDir + "/coverage/lcov.info"
                ).exists();

                String tsconfigArg = tsconfig != null
                        ? "-Dsonar.typescript.tsconfigPath=" + tsconfig
                        : "";

                String coverageArg = hasCoverage
                        ? "-Dsonar.typescript.lcov.reportPaths=coverage/lcov.info"
                        : "";

                yield """
                        set -e
                        cd %1$s
                        
                        echo "=== RUN SONAR (ANGULAR) ==="
                        echo "tsconfig: %3$s"
                        echo "coverage: %4$s"
                        
                        sonar-scanner \
                          -Dsonar.projectKey=%2$s \
                          -Dsonar.sources=src \
                          -Dsonar.exclusions=**/node_modules/**,**/*.spec.ts \
                          -Dsonar.tests=src \
                          -Dsonar.test.inclusions=**/*.spec.ts \
                          %5$s \
                          %6$s \
                          -Dsonar.host.url=%7$s \
                          -Dsonar.login=%8$s
                        """.formatted(
                        workDir,
                        projectKey,
                        tsconfig != null ? tsconfig : "DEFAULT",
                        hasCoverage ? "ENABLED" : "DISABLED",
                        tsconfigArg,
                        coverageArg,
                        sonarHost,
                        sonarToken
                );
            }

            default -> """
                    set -e
                    cd %1$s
                    
                    echo "=== RUN SONAR (GENERIC) ==="
                    sonar-scanner \
                      -Dsonar.projectKey=%2$s \
                      -Dsonar.sources=. \
                      -Dsonar.host.url=%3$s \
                      -Dsonar.login=%4$s
                    """.formatted(workDir, projectKey, sonarHost, sonarToken);
        };

        ProcessBuilder pb = new ProcessBuilder("sh", "-c", command);
        pb.inheritIO();

        int exitCode = pb.start().waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Sonar scan failed");
        }

        String ceTaskId = readCeTaskId(workDir);

        return Map.of(
                "scanId", scanId,
                "status", "SONAR_SCAN_TRIGGERED",
                "projectType", type.name(),
                "ceTaskId", ceTaskId
        );
    }

    private String resolveTsConfig(String workDir) {

        String[] candidates = {
                "tsconfig.app.json",
                "tsconfig.json",
                "tsconfig.base.json"
        };

        for (String name : candidates) {
            File f = new File(workDir, name);
            if (f.exists()) return name;
        }

        File dir = new File(workDir);
        File[] jsonc = dir.listFiles(f ->
                f.getName().startsWith("tsconfig") && f.getName().endsWith(".jsonc")
        );

        if (jsonc != null && jsonc.length > 0) {
            return jsonc[0].getName();
        }

        return null;
    }

    private String readCeTaskId(String workDir) throws Exception {

        File report = new File(workDir + "/.scannerwork/report-task.txt");

        if (!report.exists()) {
            throw new IllegalStateException("report-task.txt not found");
        }

        for (String line : java.nio.file.Files.readAllLines(report.toPath())) {
            if (line.startsWith("ceTaskId=")) {
                return line.substring("ceTaskId=".length()).trim();
            }
        }

        throw new IllegalStateException("ceTaskId not found in report-task.txt");
    }
}
