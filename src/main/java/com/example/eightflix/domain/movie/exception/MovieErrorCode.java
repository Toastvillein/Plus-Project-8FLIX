package com.example.eightflix.domain.movie.exception;

import com.example.eightflix.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum MovieErrorCode implements ErrorCode {

    // 존재하지 않는 자원
    MOVIE_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "M001", "요청한 영화를 찾을 수 없습니다.");

    private final int status;
    private final String code;
    private final String message;
}
