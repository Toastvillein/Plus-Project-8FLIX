package com.example.eightflix.domain.auth.repository;

public interface TokenRepository {
    void saveRefreshToken(String id, String refreshToken);
}
