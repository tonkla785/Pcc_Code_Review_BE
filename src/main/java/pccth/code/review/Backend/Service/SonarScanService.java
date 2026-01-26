package pccth.code.review.Backend.Service;

import org.springframework.stereotype.Service;
import pccth.code.review.Backend.DTO.Request.N8NRequestDTO;
import pccth.code.review.Backend.EnumType.ProjectTypeEnum;

import java.io.File;
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

            // sonarHost จาก env
            String sonarHost = System.getenv("SONAR_HOST_URL");
            // sonarToken มาจาก n8n payload
            String sonarToken = req.getSonarToken();

            if (sonarHost == null || sonarHost.isEmpty()) {
                throw new IllegalStateException("SONAR_HOST_URL not set in environment");
            }
            if (sonarToken == null || sonarToken.isEmpty()) {
                throw new IllegalStateException("sonarToken not provided in request");
            }

            if (type == ProjectTypeEnum.ANGULAR) {
                ensureNodeAvailable();
            }

            String command = switch (type) {

                case SPRING_BOOT -> {
                    // ใช้ค่า buildTool จาก settings (ถ้ามี) แทน auto-detect
                    var springSettings = req.getSpringSettings();
                    boolean isGradle;
                    if (springSettings != null && springSettings.getBuildTool() != null) {
                        isGradle = "gradle".equalsIgnoreCase(springSettings.getBuildTool());
                    } else {
                        // fallback: auto-detect
                        isGradle = new File(workDir + "/gradlew").exists()
                                || new File(workDir + "/build.gradle").exists()
                                || new File(workDir + "/build.gradle.kts").exists();
                    }

                    // ใช้ค่า jacoco จาก settings
                    boolean useJacoco = springSettings != null && springSettings.isJacoco();

                    String coverageArg = "";
                    if (useJacoco) {
                        if (isGradle) {
                            boolean hasJacocoGradle = new File(
                                    workDir + "/build/reports/jacoco/test/jacocoTestReport.xml").exists();
                            if (hasJacocoGradle) {
                                coverageArg = "-Dsonar.coverage.jacoco.xmlReportPaths=build/reports/jacoco/test/jacocoTestReport.xml";
                            }
                        } else {
                            boolean hasJacocoMaven = new File(
                                    workDir + "/target/site/jacoco/jacoco.xml").exists();
                            if (hasJacocoMaven) {
                                coverageArg = "-Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml";
                            }
                        }
                    }

                    // ใช้ค่า runTests จาก settings
                    boolean runTests = springSettings != null && springSettings.isRunTests();
                    String testFlag = runTests ? "" : "-DskipTests";

                    String buildCommand = isGradle
                            ? """
                                    echo "=== BUILD SPRING BOOT (GRADLE) ==="
                                    chmod +x gradlew || true
                                    ./gradlew build %s
                                    """.formatted(runTests ? "" : "-x test")
                            : """
                                    echo "=== BUILD SPRING BOOT (MAVEN) ==="
                                    if [ -f mvnw ]; then
                                      chmod +x mvnw
                                      ./mvnw -B %s compile
                                    else
                                      mvn -B %s compile
                                    fi
                                    """.formatted(testFlag, testFlag);

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
                            sonarToken);
                }

                case ANGULAR -> {
                    var angularSettings = req.getAngularSettings();
                    String tsconfig = resolveTsConfig(workDir);

                    // ใช้ค่า coverage จาก settings
                    boolean useCoverage = angularSettings != null && angularSettings.isCoverage();
                    boolean hasCoverage = useCoverage && new File(
                            workDir + "/coverage/lcov.info").exists();

                    // ใช้ค่า tsFiles จาก settings
                    boolean includeTsFiles = angularSettings != null && angularSettings.isTsFiles();

                    String tsconfigArg = (tsconfig != null && includeTsFiles)
                            ? "-Dsonar.typescript.tsconfigPath=" + tsconfig
                            : "";

                    String coverageArg = hasCoverage
                            ? "-Dsonar.typescript.lcov.reportPaths=coverage/lcov.info"
                            : "";

                    // ใช้ค่า exclusions จาก settings (ถ้ามี)
                    String exclusions = "**/node_modules/**,**/*.spec.ts";
                    if (angularSettings != null && angularSettings.getExclusions() != null
                            && !angularSettings.getExclusions().isEmpty()) {
                        exclusions = angularSettings.getExclusions();
                    }

                    exclusions = "\"" + exclusions.replace(" ", "") + "\"";

                    // ใช้ค่า runNpm จาก settings
                    boolean runNpm = angularSettings != null && angularSettings.isRunNpm();
                    String npmCommand = runNpm
                            ? """
                                    echo "=== RUNNING NPM INSTALL ==="
                                    npm install
                                    """
                            : "";

                    yield """
                            set -e
                            cd %1$s

                            %9$s

                            echo "=== RUN SONAR (ANGULAR) ==="
                            echo "tsconfig: %3$s"
                            echo "coverage: %4$s"

                            sonar-scanner \
                              -Dsonar.projectKey=%2$s \
                              -Dsonar.sources=src \
                              -Dsonar.exclusions=%10$s \
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
                            sonarToken,
                            npmCommand,
                            exclusions);
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
                    "ceTaskId", ceTaskId);
        } catch (Exception e) {
            throw new RuntimeException("Sonar scan execution error", e);
        }
    }

    private String resolveTsConfig(String workDir) {

        String[] candidates = {
                "tsconfig.app.json",
                "tsconfig.json",
                "tsconfig.base.json"
        };

        for (String name : candidates) {
            File f = new File(workDir, name);
            if (f.exists())
                return name;
        }

        File dir = new File(workDir);
        File[] jsonc = dir.listFiles(f -> f.getName().startsWith("tsconfig") && f.getName().endsWith(".jsonc"));

        if (jsonc != null && jsonc.length > 0) {
            return jsonc[0].getName();
        }

        return null;
    }

    private String readCeTaskId(String workDir) {

        try {
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

        } catch (Exception e) {
            throw new RuntimeException("Failed to read ceTaskId", e);
        }
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
