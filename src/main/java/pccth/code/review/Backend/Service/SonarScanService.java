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
                boolean hasCoverage = new File(
                        workDir + "/target/site/jacoco/jacoco.xml"
                ).exists();

                String coverageArg = hasCoverage
                        ? "-Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml"
                        : "";

                yield """
                        set -e
                        cd %1$s
                        
                        echo "=== BUILD SPRING BOOT (NO TEST) ==="
                        if [ -f mvnw ]; then
                          chmod +x mvnw
                          ./mvnw -B -DskipTests compile
                        else
                          mvn -B -DskipTests compile
                        fi
                        
                        echo "=== RUN SONAR (SPRING BOOT) ==="
                        sonar-scanner \
                          -Dsonar.projectKey=%2$s \
                          -Dsonar.sources=src/main/java \
                          -Dsonar.java.binaries=target/classes \
                          %3$s \
                          -Dsonar.host.url=%4$s \
                          -Dsonar.login=%5$s
                        """.formatted(
                        workDir,
                        projectKey,
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

        return Map.of(
                "scanId", scanId,
                "status", "SONAR_SCAN_FINISHED",
                "projectType", type.name()
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
}
