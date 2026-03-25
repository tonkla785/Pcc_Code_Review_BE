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

        String gitToken = req.getGitToken();
        boolean hasToken = gitToken != null && !gitToken.isBlank();

        try {
            String repoUrl = requireNonBlank(req.getRepositoryUrl(), "repositoryUrl");
            String branch = (req.getBranch() != null && !req.getBranch().isBlank()) ? req.getBranch() : "main";
            ProjectTypeEnum projectType = req.getProjectType();

            logs.add(new AnalysisLogEntry("Starting code review process..."));

            recreateWorkspace(workDir);

            String normalizedUrl = normalizeRepoUrl(repoUrl);
            String provider = detectGitProvider(normalizedUrl);

            logs.add(new AnalysisLogEntry(
                    hasToken
                            ? "Cloning repository (" + provider + ") with token..."
                            : "Cloning public repository (" + provider + ")..."
            ));

            //ส่ง token และ provider เข้าไปใน command
            List<String> cmd = buildGitCloneCommand(
                    normalizedUrl, branch, workDir,
                    hasToken ? gitToken : null,
                    provider
            );

            ProcessBuilder pb = new ProcessBuilder(cmd);

            pb.environment().put("GIT_TERMINAL_PROMPT", "0");

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
            logs.add(new AnalysisLogEntry("Detecting project type: " +
                    (projectType != null ? projectType.name() : "UNKNOWN")));

            Map<String, Object> data = new HashMap<>();
            data.put("workspace", workDir.toString());
            data.put("provider", provider);

            return new ServiceExecutionResult(scanId, "GIT_CLONE_SUCCESS", data, logs);

        } catch (RuntimeException re) {
            logs.add(new AnalysisLogEntry("Error: " + re.getMessage()));
            throw re;
        } catch (Exception e) {
            logs.add(new AnalysisLogEntry("Error: " + e.getMessage()));
            throw new RuntimeException("Git clone process error: " + e.getMessage(), e);
        }
    }

    // ===================== CORE =====================

    private void recreateWorkspace(Path workDir) throws Exception {
        if (Files.exists(workDir)) {
            FileSystemUtils.deleteRecursively(workDir);
        }
        Files.createDirectories(workDir);
    }

    private List<String> buildGitCloneCommand(String repoUrl, String branch, Path workDir, String token, String provider) {
        List<String> cmd = new ArrayList<>();
        cmd.add("git");
        cmd.add("-c"); cmd.add("core.askPass=");
        cmd.add("-c"); cmd.add("credential.helper=");

        // inject token ผ่าน -c http.extraheader
        if (token != null && !token.isBlank()) {
            String header = buildAuthHeader(token.trim(), provider);
            cmd.add("-c");
            cmd.add("http.extraheader=" + header);
        }

        cmd.addAll(List.of(
                "clone",
                "--depth", "1",
                "--single-branch",
                "-b", branch,
                repoUrl,
                workDir.toString()
        ));

        return cmd;
    }

    // ===================== PROVIDER =====================

    private String detectGitProvider(String url) {
        if (url.contains("github.com")) return "github";
        if (url.contains("gitlab.com")) return "gitlab";
        if (url.contains("bitbucket.org")) return "bitbucket";
        return "gitlab"; // default: self-hosted GitLab
    }

    private String buildAuthHeader(String token, String provider) {
        String raw;

        switch (provider) {
            case "github":
                raw = "x-access-token:" + token;
                break;
            case "gitlab":
                raw = "oauth2:" + token;
                break;
            case "bitbucket":
                raw = "x-token-auth:" + token;
                break;
            default:
                throw new IllegalArgumentException("Unsupported git provider for token auth");
        }

        String b64 = Base64.getEncoder().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
        return "AUTHORIZATION: basic " + b64;
    }

    // ===================== URL =====================

    private String normalizeRepoUrl(String repoUrl) {
        String url = repoUrl.trim();

        if (url.startsWith("git@")) {
            throw new IllegalArgumentException("SSH url is not supported; use HTTPS");
        }

        if (!url.startsWith("https://")) {
            throw new IllegalArgumentException("Only HTTPS repo url is supported");
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

    // ===================== SECURITY =====================

    private String maskToken(String text, String token) {
        if (text == null) return null;

        String masked = text;

        if (token != null && !token.isBlank()) {
            masked = masked.replace(token, "***");
        }

        masked = masked.replaceAll("(github_pat_[A-Za-z0-9_]+)", "github_pat_***");
        masked = masked.replaceAll("(ghp_[A-Za-z0-9]+)", "ghp_***");
        masked = masked.replaceAll("(glpat-[A-Za-z0-9\\-_]+)", "glpat-***");

        return masked;
    }

    // ===================== VALIDATION =====================

    private String requireNonBlank(String v, String field) {
        if (v == null || v.isBlank()) {
            throw new IllegalArgumentException(field + " is required");
        }
        return v;
    }
}