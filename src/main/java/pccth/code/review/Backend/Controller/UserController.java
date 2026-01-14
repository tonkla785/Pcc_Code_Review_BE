package pccth.code.review.Backend.Controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pccth.code.review.Backend.DTO.Request.LoginRequestDTO;
import pccth.code.review.Backend.DTO.Request.RegisterRequestDTO;
import pccth.code.review.Backend.DTO.Request.ResetPassDTO;
import pccth.code.review.Backend.DTO.Response.AccessTokenResponseDTO;
import pccth.code.review.Backend.DTO.Response.LoginResponseDTO;
import pccth.code.review.Backend.DTO.Response.RegisterResponseDTO;
import pccth.code.review.Backend.Service.AuthService;
import pccth.code.review.Backend.Service.UserService;
import pccth.code.review.Backend.Util.CookieUtil;

@RestController
@RequestMapping("/user")
public class UserController {

    private final AuthService authService;
    private final UserService userService;

    public UserController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        RegisterResponseDTO response = userService.register(request);
        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request,
                                                  HttpServletResponse response) {
        LoginResponseDTO loginResponse = userService.login(request, response);
        return ResponseEntity.ok(loginResponse);
    }

    //postman test
    @PostMapping("/refresh")
    public ResponseEntity<AccessTokenResponseDTO> refreshAccessToken(@CookieValue(name = "refresh_token", required = false) String refreshToken) {
        AccessTokenResponseDTO newAccessToken = authService.refreshAccessToken(refreshToken);
        return ResponseEntity.ok(newAccessToken);
    }

    @PutMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPassDTO request){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        userService.resetPassword(username, request);
        return ResponseEntity.ok("Password reset successfully");
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@CookieValue(name = "refresh_token", required = false) String refreshToken, HttpServletResponse response) {
        authService.logout(refreshToken);
        response.addHeader("Cookie", CookieUtil.clearRefreshTokenCookie().toString());
        return ResponseEntity.ok("Logged out successfully");
    }
}
