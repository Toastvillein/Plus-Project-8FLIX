package com.example.eightflix.domain.user.dto;

import com.example.eightflix.domain.user.entity.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotNull;

public record SignUpRequest (
    @NotBlank
    String userId,
    @NotBlank
    @Email(message = "유효한 이메일 형식이어야 합니다.")
    String email,
    @NotBlank
    String password,
    @NotBlank
    String nickname,
    @NotBlank
    @Pattern(regexp = "^01[016789]-\\d{3,4}-\\d{4}$", message = "전화번호는 010-1234-5678 형식이어야 합니다.")
    String phoneNumber,
    @NotNull
    UserRole role
) {
}
