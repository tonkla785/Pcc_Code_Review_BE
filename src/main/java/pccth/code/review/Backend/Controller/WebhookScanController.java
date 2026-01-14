package pccth.code.review.Backend.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pccth.code.review.Backend.DTO.Request.N8NRequestDTO;
import pccth.code.review.Backend.Service.GitCloneService;
import pccth.code.review.Backend.Service.SonarScanService;

@RestController
@RequestMapping("/webhooks/scan")
public class WebhookScanController {

    private final GitCloneService gitCloneService;
    private final SonarScanService sonarScanService;

    public WebhookScanController(
            GitCloneService gitCloneService,
            SonarScanService sonarScanService
    ) {
        this.gitCloneService = gitCloneService;
        this.sonarScanService = sonarScanService;
    }

    @PostMapping("/git-clone")
    public ResponseEntity<?> gitClone(@RequestBody N8NRequestDTO req) throws Exception {
        return ResponseEntity.ok(gitCloneService.execute(req));
    }

    @PostMapping("/sonar-scan")
    public ResponseEntity<?> sonarScan(@RequestBody N8NRequestDTO req) throws Exception {
        return ResponseEntity.ok(sonarScanService.execute(req));
    }
}