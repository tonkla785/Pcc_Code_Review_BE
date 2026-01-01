package pccth.code.review.Backend.Util;

import org.springframework.http.ResponseCookie;

import java.time.Duration;

public class CookieUtil {

    private static final String REFRESH_TOKEN_NAME = "refresh_token";

    public static ResponseCookie createRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from(REFRESH_TOKEN_NAME, refreshToken)
                .httpOnly(true)
                .secure(false) //dev อยู่ product ค่อย เปลี่ยน
                .sameSite("Strict")
                .path("/user/refresh")
                .maxAge(Duration.ofDays(7))
                .build();
    }

    public static ResponseCookie clearRefreshTokenCookie() {
        return ResponseCookie.from(REFRESH_TOKEN_NAME, "")
                .path("/user/refresh")
                .maxAge(0)
                .build();
    }
}
