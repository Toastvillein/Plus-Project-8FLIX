package com.example.eightflix.domain.auth.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class RedisTokenRepository implements TokenRepository{

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void saveRefreshToken(String id, String refreshToken) {
        redisTemplate.opsForValue().set(
                "userId:" + id,
                "refreshToken:" + refreshToken,
                Duration.ofMillis(refreshTokenExpiration));
    }

    @Override
    public boolean isRefreshTokenValid(String id, String refreshToken) {
        String key = "userId:" + id;
        Object storedToken = redisTemplate.opsForValue().get(key);

        return ("refreshToken:" + refreshToken).equals(String.valueOf(storedToken));
    }

    @Override
    public void deleteRefreshToken(String id) {
        redisTemplate.delete("userId:" + id);
    }
}
