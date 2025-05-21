package com.example.eightflix.domain.user.controller;


import com.example.eightflix.domain.user.dto.GetUserResponse;
import com.example.eightflix.domain.user.dto.SignUpRequest;
import com.example.eightflix.domain.user.service.UserService;
import com.example.eightflix.global.security.CurrentUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/api/auth/signup")
    public ResponseEntity<Void> signup(@Valid @RequestBody SignUpRequest signUpRequest) {
        userService.signUp(signUpRequest);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/api/users")
    public ResponseEntity<GetUserResponse> getUser(@CurrentUser Long id) {
        return ResponseEntity.ok(userService.getUser(id));
    }
}
