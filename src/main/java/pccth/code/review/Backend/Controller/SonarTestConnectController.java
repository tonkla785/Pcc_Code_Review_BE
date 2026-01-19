package pccth.code.review.Backend.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pccth.code.review.Backend.DTO.Request.SonarTestConnectRequestDTO;
import pccth.code.review.Backend.Service.SonarTestConnectService;

import java.util.Map;

@RestController
@RequestMapping("/sonar")
public class SonarTestConnectController {
    private final SonarTestConnectService SonarTestConnectService;

    public SonarTestConnectController(SonarTestConnectService SonarTestConnectService) {
        this.SonarTestConnectService = SonarTestConnectService;
    }

    @PostMapping("/test-connect")
    public ResponseEntity<?> validate(@RequestBody SonarTestConnectRequestDTO req) {

        boolean connected = SonarTestConnectService.validateSonarToken(
                req.getSonarHostUrl(),
                req.getSonarToken()
        );

        return ResponseEntity.ok(Map.of(
                "connected", connected
        ));
    }

}
