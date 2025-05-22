package com.example.eightflix.domain.auth.service;

import com.example.eightflix.domain.auth.dto.SignInRequest;
import com.example.eightflix.domain.auth.dto.TokenPair;
import com.example.eightflix.domain.auth.repository.TokenRepository;
import com.example.eightflix.domain.auth.util.JwtUtil;
import com.example.eightflix.domain.auth.util.PasswordEncoder;
import com.example.eightflix.domain.user.entity.User;
import com.example.eightflix.domain.user.exception.UserErrorCode;
import com.example.eightflix.domain.user.repository.UserRepository;
import com.example.eightflix.global.exception.BizException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final TokenRepository tokenRepository;

    public TokenPair signIn(SignInRequest request) {
        User user = userRepository.findByUserId(request.userId())
                .orElseThrow(() -> new BizException(UserErrorCode.NOT_FOUND_USER));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BizException(UserErrorCode.INVALID_PASSWORD);
        }
        return getTokenPair(user);
    }

    private TokenPair getTokenPair(User user) {
        String accessToken = jwtUtil.createAccessToken(user.getId(), user.getEmail(), user.getNickname(), user.getRole());
        String refreshToken = jwtUtil.createRefreshToken(user.getId());
        String refreshTokenSubString =  jwtUtil.substringToken(refreshToken);
        tokenRepository.saveRefreshToken(String.valueOf(user.getId()), refreshTokenSubString);

        return new TokenPair(accessToken, refreshTokenSubString);
    }

    public TokenPair reissue(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BizException(UserErrorCode.NOT_FOUND_USER));
        return getTokenPair(user);
    }
}
