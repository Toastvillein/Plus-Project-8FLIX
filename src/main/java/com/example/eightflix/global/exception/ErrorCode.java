package com.example.eightflix.global.exception;

public interface ErrorCode {
	int getStatus();

	String getCode();

	String getMessage();
}
