package com.example.eightflix.domain.auth.exception;

import com.example.eightflix.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {
    NOT_FOUND_TOKEN(HttpStatus.UNAUTHORIZED,"토큰이 유효한 형태가 아닙니다.");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public int getStatus() {
        return httpStatus.value();
    }

    @Override
    public String getCode() {
        return this.name();
    }

    @Override
    public String getMessage() {
        return message;
    }
}
