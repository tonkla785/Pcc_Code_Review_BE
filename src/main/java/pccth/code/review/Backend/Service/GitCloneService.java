package pccth.code.review.Backend.Service;

import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import pccth.code.review.Backend.DTO.Request.N8NRequestDTO;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@Service
public class GitCloneService {

    private static final Path BASE_DIR = Paths.get("/scan-workspace");

    public Map<String, Object> execute(N8NRequestDTO req) {

        try {
            UUID scanId = req.getScanId();
            String repoUrl = req.getRepositoryUrl();
            String branch = req.getBranch() != null ? req.getBranch() : "main";

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
                    workDir.toString()
            );

            pb.inheritIO();
            int code = pb.start().waitFor();
            if (code != 0) {
                throw new RuntimeException("Git clone failed");
            }

            return Map.of(
                    "scanId", scanId,
                    "workspace", workDir.toString()
            );
        } catch (Exception e) {
            throw new RuntimeException("Git clone process error", e);
        }
    }
}