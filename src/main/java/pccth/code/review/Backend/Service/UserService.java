package pccth.code.review.Backend.Service;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pccth.code.review.Backend.DTO.Request.LoginRequestDTO;
import pccth.code.review.Backend.DTO.Request.RegisterRequestDTO;
import pccth.code.review.Backend.DTO.Request.ResetPassDTO;
import pccth.code.review.Backend.DTO.Response.LoginResponseDTO;
import pccth.code.review.Backend.DTO.Response.RegisterResponseDTO;
import pccth.code.review.Backend.Entity.UserEntity;
import pccth.code.review.Backend.Repository.UserRepository;
import pccth.code.review.Backend.Util.CookieUtil;

import java.util.Date;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    @Transactional
    public RegisterResponseDTO register(RegisterRequestDTO request) {
        validateDuplicateUser(request);

        UserEntity user = new UserEntity();
        user.setId(UUID.randomUUID());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setRole("USER");
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setCreateAt(new Date());

        userRepository.save(user);

        RegisterResponseDTO response = new RegisterResponseDTO();
        response.setMessage("User registered successfully");
        return response;
    }

    public LoginResponseDTO login(LoginRequestDTO request, HttpServletResponse response) {
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid email or password");
        }

        // สร้าง Access Token
        String accessToken = jwtService.generateAccessToken(user.getUsername());

        // สร้าง Refresh Token
        String refreshToken = jwtService.generateRefreshToken(user.getUsername());
        refreshTokenService.save(user.getUsername(), refreshToken);

        // สร้าง Cookie สำหรับ Refresh Token
        response.addHeader("Cookie", CookieUtil.createRefreshTokenCookie(refreshToken).toString());

        // สร้าง Response DTO
        LoginResponseDTO loginResponse = new LoginResponseDTO();
        loginResponse.setAccessToken(accessToken);
        loginResponse.setId(user.getId().toString());
        loginResponse.setUsername(user.getUsername());
        loginResponse.setEmail(user.getEmail());
        loginResponse.setPhone(user.getPhone());
        loginResponse.setRole(user.getRole());
        return loginResponse;
    }

    private void validateDuplicateUser(RegisterRequestDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already in use");
        }

        if (userRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException("Phone number already in use");
        }
    }

    public void resetPassword(String username, ResetPassDTO request) {

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(
                request.getCurrentPassword(),
                user.getPasswordHash())) {
            throw new RuntimeException("Current password is incorrect");
        }

        user.setPasswordHash(
                passwordEncoder.encode(request.getNewPassword())
        );

        userRepository.save(user);
    }

}
