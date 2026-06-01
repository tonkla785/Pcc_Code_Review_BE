package pccth.code.review.Backend.Util;

import jakarta.servlet.http.HttpServletRequest;

import java.net.URI;
import java.util.List;

/**
 * ดึง Frontend Origin และ Backend Base URL จาก HTTP Request แบบ Dynamic
 * พร้อม validate กับ CORS whitelist เพื่อป้องกัน Open Redirect / Host Header Injection
 */
public final class RequestOriginUtil {

    private static final String DEFAULT_FRONTEND = "http://localhost:4200";
    private static final String DEFAULT_BACKEND  = "http://localhost:8080";

    private RequestOriginUtil() {}

    /**
     * ดึง Frontend Origin จาก Origin header (หรือ Referer fallback)
     * แล้ว validate กับ CORS whitelist
     *
     * @param request         HttpServletRequest ที่เข้ามา
     * @param allowedOrigins  CORS allowed origins list จาก config
     * @return frontend base URL เช่น "http://localhost:4200" หรือ "https://gpt.pccth.com"
     */
    public static String extractFrontendOrigin(HttpServletRequest request, List<String> allowedOrigins) {
        // 1) ลอง Origin header ก่อน (ส่งมาเสมอใน cross-origin request)
        String origin = request.getHeader("Origin");
        if (origin != null && !origin.isBlank()) {
            String trimmed = origin.trim();
            if (isAllowed(trimmed, allowedOrigins)) {
                return trimmed;
            }
        }

        // 2) Fallback เป็น Referer header (same-origin request อาจไม่มี Origin)
        String referer = request.getHeader("Referer");
        if (referer != null && !referer.isBlank()) {
            try {
                URI uri = URI.create(referer.trim());
                String refOrigin = uri.getScheme() + "://" + uri.getAuthority();
                if (isAllowed(refOrigin, allowedOrigins)) {
                    return refOrigin;
                }
            } catch (Exception ignored) {
                // Referer parse failed — fall through to default
            }
        }

        // 3) Default fallback
        return DEFAULT_FRONTEND;
    }

    /**
     * สร้าง Backend Base URL จาก Request (รองรับ reverse proxy ผ่าน X-Forwarded-* headers)
     *
     * @param request  HttpServletRequest ที่เข้ามา
     * @return backend base URL เช่น "http://localhost:8080" หรือ "https://gpt.pccth.com"
     */
    public static String extractBackendBaseUrl(HttpServletRequest request) {
        // รองรับ reverse proxy (Nginx) ที่ส่ง X-Forwarded-* มา
        String proto = request.getHeader("X-Forwarded-Proto");
        String host  = request.getHeader("X-Forwarded-Host");

        if (proto != null && !proto.isBlank() && host != null && !host.isBlank()) {
            return proto.trim() + "://" + host.trim();
        }

        // Fallback: ใช้ค่าจาก request โดยตรง
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int port = request.getServerPort();

        // ซ่อน port ถ้าเป็น default (80 สำหรับ http, 443 สำหรับ https)
        if ((port == 80 && "http".equals(scheme)) || (port == 443 && "https".equals(scheme))) {
            return scheme + "://" + serverName;
        }
        return scheme + "://" + serverName + ":" + port;
    }

    /**
     * Validate origin จาก query parameter (ใช้ตอน redirect หลัง confirm email)
     * ป้องกัน Open Redirect โดย check กับ CORS whitelist
     *
     * @param origin          origin URL ที่ต้อง validate
     * @param allowedOrigins  CORS allowed origins list จาก config
     * @return origin ที่ผ่าน validation แล้ว หรือ default ถ้าไม่ผ่าน
     */
    public static String validateOrigin(String origin, List<String> allowedOrigins) {
        if (origin == null || origin.isBlank()) {
            return DEFAULT_FRONTEND;
        }
        String trimmed = origin.trim();
        if (isAllowed(trimmed, allowedOrigins)) {
            return trimmed;
        }
        return DEFAULT_FRONTEND;
    }

    private static boolean isAllowed(String origin, List<String> allowedOrigins) {
        if (allowedOrigins == null || allowedOrigins.isEmpty()) {
            return false;
        }
        for (String allowed : allowedOrigins) {
            if (allowed.trim().equalsIgnoreCase(origin)) {
                return true;
            }
        }
        return false;
    }
}
