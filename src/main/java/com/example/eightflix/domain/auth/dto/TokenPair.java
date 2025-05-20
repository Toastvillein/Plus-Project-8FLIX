package com.example.eightflix.domain.auth.dto;

public record TokenPair (
        String accessToken,
        String refreshToken
){
}
