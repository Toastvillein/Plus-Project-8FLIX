package com.example.eightflix.domain.user.exception;

import com.example.eightflix.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {
    DUPLICATE_PHONE_NUMBER(HttpStatus.BAD_REQUEST,"중복된 전화번호입니다."),
    DUPLICATE_USER_ID(HttpStatus.BAD_REQUEST,"중복된 아이디입니다.");

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
