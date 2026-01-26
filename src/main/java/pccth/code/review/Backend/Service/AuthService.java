package pccth.code.review.Backend.Service;

import org.springframework.stereotype.Service;
import pccth.code.review.Backend.DTO.Response.AccessTokenResponseDTO;
import pccth.code.review.Backend.Entity.UserEntity;
import pccth.code.review.Backend.Repository.UserRepository;

@Service
public class AuthService {

    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;

    public AuthService(JwtService jwtService,
                       RefreshTokenService refreshTokenService,UserRepository userRepository) {
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.userRepository = userRepository;
    }

    public AccessTokenResponseDTO refreshAccessToken(String refreshToken) {

        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new RuntimeException("Refresh token missing");
        }

        String username = jwtService.extractUsername(refreshToken);

        if (!refreshTokenService.validate(username, refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String newAccessToken =
                jwtService.generateAccessToken(user.getUsername(), user.getRole());

        AccessTokenResponseDTO response = new AccessTokenResponseDTO();
        response.setAccessToken(newAccessToken);
        return response;
    }

    public void logout(String refreshToken) {
        if (refreshToken != null && !refreshToken.isEmpty()) {
            String username = jwtService.extractUsername(refreshToken);
            refreshTokenService.deleteByUsername(username);
        }
    }
}
