package pccth.code.review.Backend.Service;

import org.springframework.stereotype.Service;
import pccth.code.review.Backend.DTO.Response.AccessTokenResponseDTO;

@Service
public class AuthService {

    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthService(JwtService jwtService,
                       RefreshTokenService refreshTokenService) {
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    public AccessTokenResponseDTO refreshAccessToken(String refreshToken) {
        // ตรวจสอบว่า refresh token ถูกส่งมาหรือไม่
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new RuntimeException("Refresh token missing");
        }

        // ดึง username จาก refresh token
        String username = jwtService.extractUsername(refreshToken);

        // ตรวจสอบว่า token ถูกเก็บใน Redis และตรงกับของผู้ใช้
        if (!refreshTokenService.validate(username, refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        // สร้าง access token ใหม่
        AccessTokenResponseDTO response = new AccessTokenResponseDTO();
        response.setAccessToken(jwtService.generateAccessToken(username));
        return response;
    }
}
