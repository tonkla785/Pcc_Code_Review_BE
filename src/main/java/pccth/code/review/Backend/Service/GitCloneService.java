package pccth.code.review.Backend.Service;

import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import pccth.code.review.Backend.DTO.AnalysisLogEntry;
import pccth.code.review.Backend.DTO.Request.N8NRequestDTO;
import pccth.code.review.Backend.DTO.Response.ServiceExecutionResult;
import pccth.code.review.Backend.EnumType.ProjectTypeEnum;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.Base64;

@Service
public class GitCloneService {

    private static final Path BASE_DIR = Paths.get("/scan-workspace");

    public ServiceExecutionResult execute(N8NRequestDTO req) {
        List<AnalysisLogEntry> logs = new ArrayList<>();
        UUID scanId = req.getScanId();

        Path workDir = BASE_DIR.resolve(scanId.toString()).normalize();
        if (!workDir.startsWith(BASE_DIR)) {
            throw new SecurityException("Invalid workspace path");
        }

        String gitToken = null;
        boolean hasToken = false;

        try {
            String repoUrl = requireNonBlank(req.getRepositoryUrl(), "repositoryUrl");
            String branch = (req.getBranch() != null && !req.getBranch().isBlank()) ? req.getBranch() : "main";
            ProjectTypeEnum projectType = req.getProjectType();

            gitToken = req.getGitToken();
            hasToken = gitToken != null && !gitToken.isBlank();

            logs.add(new AnalysisLogEntry("Starting code review process..."));

            // recreate workspace
            if (Files.exists(workDir)) {
                FileSystemUtils.deleteRecursively(workDir);
            }
            Files.createDirectories(workDir);

            String cloneUrl = normalizePublicRepoUrl(repoUrl);

            logs.add(new AnalysisLogEntry(
                    hasToken
                            ? "Cloning repository from GitHub (with token)..."
                            : "Cloning public repository from GitHub..."
            ));

            List<String> cmd = new ArrayList<>();
            cmd.add("git");

            // ปิด prompt/credential helper
            cmd.add("-c"); cmd.add("core.askPass=");
            cmd.add("-c"); cmd.add("credential.helper=");

            // ✅ GitHub: ใช้ Basic auth "x-access-token:<PAT>"
            if (hasToken) {
                String basic = buildGithubBasicAuthHeader(gitToken.trim()); // "basic <base64>"
                cmd.add("-c");
                cmd.add("http.extraheader=AUTHORIZATION: " + basic);
            }

            cmd.addAll(List.of(
                    "clone",
                    "--depth", "1",
                    "--single-branch",
                    "-b", branch,
                    cloneUrl,
                    workDir.toString()
            ));

            ProcessBuilder pb = new ProcessBuilder(cmd);

            // กัน prompt
            pb.environment().put("GIT_TERMINAL_PROMPT", "0");
            pb.environment().put("GIT_ASKPASS", "/bin/false");

            pb.redirectErrorStream(true);

            Process p = pb.start();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            p.getInputStream().transferTo(baos);

            int code = p.waitFor();
            String out = baos.toString(StandardCharsets.UTF_8);

            if (code != 0) {
                String safe = maskToken(out, gitToken);
                logs.add(new AnalysisLogEntry("Git clone failed: " + safe));

                if (!hasToken) {
                    throw new RuntimeException("Git clone failed: " + safe + " (repo may be private—please provide gitToken)");
                }
                throw new RuntimeException("Git clone failed: " + safe);
            }

            logs.add(new AnalysisLogEntry("Repository cloned successfully"));
            logs.add(new AnalysisLogEntry("Detecting project type: " + (projectType != null ? projectType.name() : "UNKNOWN")));

            Map<String, Object> data = new HashMap<>();
            data.put("workspace", workDir.toString());

            return new ServiceExecutionResult(scanId, "GIT_CLONE_SUCCESS", data, logs);

        } catch (RuntimeException re) {
            logs.add(new AnalysisLogEntry("Error: " + re.getMessage()));
            throw re;
        } catch (Exception e) {
            logs.add(new AnalysisLogEntry("Error: " + e.getMessage()));
            throw new RuntimeException("Git clone process error: " + e.getMessage(), e);
        }
    }

    private static String requireNonBlank(String v, String field) {
        if (v == null || v.isBlank()) throw new IllegalArgumentException(field + " is required");
        return v;
    }

    private static String normalizePublicRepoUrl(String repoUrl) {
        String url = repoUrl.trim();

        if (url.startsWith("git@")) {
            throw new IllegalArgumentException("SSH url is not supported here; use https repo url");
        }
        if (!url.startsWith("https://")) {
            throw new IllegalArgumentException("Only https repo url is supported");
        }

        URI uri = URI.create(url);

        String path = uri.getPath();
        if (path == null || path.isBlank()) {
            throw new IllegalArgumentException("Invalid repositoryUrl");
        }
        if (!path.endsWith(".git")) {
            path = path + ".git";
        }

        return uri.getScheme() + "://" + uri.getHost() + path;
    }

    private static String buildGithubBasicAuthHeader(String token) {
        // basic base64("x-access-token:<PAT>")
        String raw = "x-access-token:" + token;
        String b64 = Base64.getEncoder().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
        return "basic " + b64;
    }

    private static String maskToken(String text, String token) {
        if (text == null) return null;
        String masked = text;

        if (token != null && !token.isBlank()) {
            masked = masked.replace(token, "***");
        }

        masked = masked.replaceAll("(github_pat_[A-Za-z0-9_]+)", "github_pat_***");
        masked = masked.replaceAll("(ghp_[A-Za-z0-9]+)", "ghp_***");

        return masked;
    }
}
