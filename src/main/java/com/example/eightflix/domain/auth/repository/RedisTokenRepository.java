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

    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void saveRefreshToken(String id, String refreshToken) {
        redisTemplate.opsForValue().set(
                "userId:" + id,
                "refreshToken:" + refreshToken,
                Duration.ofMillis(refreshTokenExpiration));
    }

    public void setRefreshTokenExpiration(long refreshTokenExpiration) {
        this.refreshTokenExpiration = refreshTokenExpiration;
    }
}
