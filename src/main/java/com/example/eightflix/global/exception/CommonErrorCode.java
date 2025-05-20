package com.example.eightflix.global.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode {
	INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST.value(), "C001", "입력값이 올바르지 않습니다."),

	// 인증 및 권한 오류
	UNAUTHORIZED_REVIEW_ACCESS(HttpStatus.FORBIDDEN.value(), "R003", "해당 리뷰에 대한 접근 권한이 없습니다."),
	REVIEW_OWNER_MISMATCH(HttpStatus.FORBIDDEN.value(), "R004", "본인의 리뷰만 수정하거나 삭제할 수 있습니다."),

	// 존재하지 않는 자원
	REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "R005", "요청한 리뷰를 찾을 수 없습니다."),
	USER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "R006", "작성자 정보를 찾을 수 없습니다."),
	PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "R007", "리뷰 대상 상품을 찾을 수 없습니다."),

	// 중복 및 제약 조건
	DUPLICATE_REVIEW(HttpStatus.CONFLICT.value(), "R008", "이미 해당 상품에 대한 리뷰를 작성하셨습니다."),

	// 서버 오류
	REVIEW_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "R009", "리뷰 저장에 실패했습니다."),
	REVIEW_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "R010", "리뷰 삭제에 실패했습니다.");


	private final int status;
	private final String code;
	private final String message;
}
