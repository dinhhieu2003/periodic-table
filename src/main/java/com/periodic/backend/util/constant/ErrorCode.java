package com.periodic.backend.util.constant;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public enum ErrorCode {
	UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
	INVALID_KEY(1001, "Invalid key error", HttpStatus.BAD_REQUEST),
	USER_ALREADY_EXISTS(1002, "Email already exists", HttpStatus.BAD_REQUEST),
	UNAUTHENTICATED(1003, "Unauthenticated access", HttpStatus.UNAUTHORIZED),
	UNAUTHORIZED(1004, "You do not have permission", HttpStatus.FORBIDDEN),
	TOKEN_NOT_VALID(1005, "Token is not valid", HttpStatus.BAD_REQUEST),
	MISSING_COOKIE(1006, "Required cookie is missing", HttpStatus.BAD_REQUEST),
	USER_NOT_FOUND(1007, "User not found", HttpStatus.NOT_FOUND),
	USER_IS_INACTIVE(1008, "Account has been disabled", HttpStatus.FORBIDDEN),
	OTP_NOT_FOUND(1009, "OTP has been expired or wrong", HttpStatus.NOT_FOUND),
	PASSWORDCONFIRM_NOT_MATCH(1010, "Password and confirm password not match", HttpStatus.BAD_REQUEST);
	
	private final int code;
	private final String message;
	private final HttpStatusCode statusCode;
	
	ErrorCode(int code, String message, HttpStatusCode statusCode) {
		this.code = code;
		this.message = message;
		this.statusCode = statusCode;
	}
	
	
}
