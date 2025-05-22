package com.example.eightflix.domain.reservation.exception;

import org.springframework.http.HttpStatus;

import com.example.eightflix.global.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReservationErrorCode implements ErrorCode {
	UNAVAILABLE_SEAT_COUNT_ERROR(HttpStatus.BAD_REQUEST, "예약 가능한 좌석 개수가 아닙니다."),
	UNAVAILABLE_SEAT_ERROR(HttpStatus.BAD_REQUEST, "예약할 수 없는 좌석입니다."),
	ALREADY_RESERVED_SEAT_ERROR(HttpStatus.BAD_REQUEST, "이미 예약된 좌석입니다.");

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
