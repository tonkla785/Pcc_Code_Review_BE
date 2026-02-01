package pccth.code.review.Backend.Service;

import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import pccth.code.review.Backend.DTO.AnalysisLogEntry;
import pccth.code.review.Backend.DTO.Request.N8NRequestDTO;
import pccth.code.review.Backend.DTO.Response.ServiceExecutionResult;
import pccth.code.review.Backend.EnumType.ProjectTypeEnum;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class GitCloneService {

    private static final Path BASE_DIR = Paths.get("/scan-workspace");

    public ServiceExecutionResult execute(N8NRequestDTO req) {
        List<AnalysisLogEntry> logs = new ArrayList<>();

        try {
            UUID scanId = req.getScanId();
            String repoUrl = req.getRepositoryUrl();
            String branch = req.getBranch() != null ? req.getBranch() : "main";
            ProjectTypeEnum projectType = req.getProjectType();

            logs.add(new AnalysisLogEntry("Starting code review process..."));
            logs.add(new AnalysisLogEntry("Cloning repository from GitHub..."));

            Path workDir = BASE_DIR.resolve(scanId.toString()).normalize();
            if (!workDir.startsWith(BASE_DIR)) {
                throw new SecurityException("Invalid workspace path");
            }

            if (Files.exists(workDir)) {
                FileSystemUtils.deleteRecursively(workDir);
            }
            Files.createDirectories(workDir);

            ProcessBuilder pb = new ProcessBuilder(
                    "git", "clone",
                    "--depth", "1",
                    "-b", branch,
                    repoUrl,
                    workDir.toString());

            pb.inheritIO();
            int code = pb.start().waitFor();
            if (code != 0) {
                throw new RuntimeException("Git clone failed");
            }

            logs.add(new AnalysisLogEntry("Repository cloned successfully"));
            logs.add(new AnalysisLogEntry("Detecting project type: " + projectType.name()));

            Map<String, Object> data = new HashMap<>();
            data.put("workspace", workDir.toString());

            return new ServiceExecutionResult(scanId, "GIT_CLONE_SUCCESS", data, logs);

        } catch (Exception e) {
            logs.add(new AnalysisLogEntry("Error: " + e.getMessage()));
            throw new RuntimeException("Git clone process error", e);
        }
    }
}
