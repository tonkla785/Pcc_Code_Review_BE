package pccth.code.review.Backend.Service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import pccth.code.review.Backend.Config.JwtConfig;

import java.util.concurrent.TimeUnit;

@Service
public class RefreshTokenService {

    private static final String KEY_PREFIX = "refresh:";
    private final JwtConfig jwtConfig;

    private final RedisTemplate<String, String> redisTemplate;

    public RefreshTokenService(RedisTemplate<String, String> redisTemplate, JwtConfig jwtConfig) {
        this.redisTemplate = redisTemplate;
        this.jwtConfig = jwtConfig;
    }

    public void save(String username, String refreshToken) {
        try {
            redisTemplate.opsForValue()
                    .set(
                            KEY_PREFIX + username,
                            refreshToken,
                            jwtConfig.getRefreshExpiration() / 1000,
                            TimeUnit.SECONDS
                    );
        } catch (Exception e) {
            throw new RuntimeException("Error while saving refresh token", e);
        }
    }

    public String findByUsername(String username) {
        try {
            return redisTemplate.opsForValue()
                    .get(KEY_PREFIX + username);
        } catch (Exception e) {
            throw new RuntimeException("Error while finding refresh token", e);
        }
    }

    public void deleteByUsername(String username) {
        try {
            redisTemplate.delete(KEY_PREFIX + username);
        } catch (Exception e) {
            throw new RuntimeException("Error while deleting refresh token", e);
        }
    }

    public boolean validate(String username, String refreshToken) {
        String storedToken = findByUsername(username);
        return storedToken != null && storedToken.equals(refreshToken);
    }
}

