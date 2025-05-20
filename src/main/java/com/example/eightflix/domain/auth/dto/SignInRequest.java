package com.example.eightflix.domain.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SignInRequest(
        @NotBlank
        String userId,
        @NotBlank
        String password
) {
}
