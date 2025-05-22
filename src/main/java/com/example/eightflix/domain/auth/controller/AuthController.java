package com.example.eightflix.domain.auth.controller;

import com.example.eightflix.domain.auth.dto.ReissueResponse;
import com.example.eightflix.domain.auth.dto.SignInRequest;
import com.example.eightflix.domain.auth.dto.SignInResponse;
import com.example.eightflix.domain.auth.dto.TokenPair;
import com.example.eightflix.domain.auth.service.AuthService;
import com.example.eightflix.global.security.CurrentUser;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @Value("${jwt.refresh-token-expiration}")
    private int refreshTokenExpiration;

    @PostMapping("/api/auth/signin")
    public ResponseEntity<SignInResponse> signIn(@Valid @RequestBody SignInRequest signInRequest, HttpServletResponse response) {
        TokenPair tokenPair = authService.signIn(signInRequest);
        Cookie cookie = new Cookie("refreshToken", tokenPair.refreshToken());
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(refreshTokenExpiration);
        response.addCookie(cookie);
        return ResponseEntity.ok(new SignInResponse(tokenPair.accessToken()));
    }

    @PostMapping("/api/auth/reissue")
    public ResponseEntity<ReissueResponse> reissue(@CurrentUser Long id, HttpServletResponse httpResponse) {
        TokenPair tokenPair = authService.reissue(id);
        Cookie cookie = new Cookie("refreshToken", tokenPair.refreshToken());
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(refreshTokenExpiration);
        httpResponse.addCookie(cookie);
        return ResponseEntity.ok(new ReissueResponse(tokenPair.accessToken()));
    }

}
