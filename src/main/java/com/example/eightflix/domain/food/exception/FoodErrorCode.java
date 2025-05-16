package com.example.eightflix.domain.food.exception;

import org.springframework.http.HttpStatus;

import com.example.eightflix.global.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FoodErrorCode implements ErrorCode {
	INVALID_ID(HttpStatus.BAD_REQUEST, "유효하지 않은 아이디입니다."),
	INVALID_NAME(HttpStatus.BAD_REQUEST, "중복된 이름입니다.");



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
