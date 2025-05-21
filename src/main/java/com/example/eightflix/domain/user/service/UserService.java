package com.example.eightflix.domain.user.service;

import com.example.eightflix.domain.auth.util.PasswordEncoder;
import com.example.eightflix.domain.user.dto.GetUserResponse;
import com.example.eightflix.domain.user.dto.SignUpRequest;
import com.example.eightflix.domain.user.entity.User;
import com.example.eightflix.domain.user.exception.UserErrorCode;
import com.example.eightflix.domain.user.repository.UserRepository;
import com.example.eightflix.global.exception.BizException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void signUp(SignUpRequest request) {
        if (userRepository.existsByPhoneNumber(request.phoneNumber())) {
            throw new BizException(UserErrorCode.DUPLICATE_PHONE_NUMBER);
        }

        if (userRepository.existsByUserId(request.userId())) {
            throw new BizException(UserErrorCode.DUPLICATE_USER_ID);
        }

        String encodedPassword = passwordEncoder.encode(request.password());

        User user = User.builder()
                .userId(request.userId())
                .email(request.email())
                .password(encodedPassword)
                .nickname(request.nickname())
                .phoneNumber(request.phoneNumber())
                .role(request.role())
                .build();

        userRepository.save(user);
    }

    public GetUserResponse getUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BizException(UserErrorCode.NOT_FOUND_USER));

        return new GetUserResponse(user.getNickname(),user.getPhoneNumber());
    }
}
