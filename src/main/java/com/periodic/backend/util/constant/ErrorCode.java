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
	PASSWORDCONFIRM_NOT_MATCH(1010, "Password and confirm password not match", HttpStatus.BAD_REQUEST),
	
	// ====== ELEMENT =====
	ELEMENT_ALREADY_EXISTS(2001, "Element is already exists", HttpStatus.BAD_REQUEST),
	ELEMENT_NOT_FOUND(2002, "Element not found", HttpStatus.NOT_FOUND),
	
	// ====== SCIENTIST ====
	SCIENTIST_ALREADY_EXISTS(3001, "Scientist is already exists", HttpStatus.BAD_REQUEST),
	SCIENTIST_NOT_FOUND(3002, "Scientist not found", HttpStatus.NOT_FOUND),
	
	// ====== PODCAST ======
	PODCAST_ALREADY_EXISTS(4001, "Podcast is already exists", HttpStatus.BAD_REQUEST),
	PODCAST_NOT_FOUND(4002, "Podcast not found", HttpStatus.NOT_FOUND),
	
	// ====== DISCOVERY ======
	DISCOVERY_ALREADY_EXISTS(5001, "Discovery relationship already exists", HttpStatus.BAD_REQUEST),
	DISCOVERY_NOT_FOUND(5002, "Discovery not found", HttpStatus.NOT_FOUND),
	
	// ====== MILESTONE ======
	MILESTONE_NOT_FOUND(6001, "Milestone not found", HttpStatus.NOT_FOUND),
	
	// ====== COMMENT ======
	COMMENT_NOT_FOUND(7001, "Comment not found", HttpStatus.NOT_FOUND),
	NOT_AUTHORIZED(7002, "Not authorized to perform this action", HttpStatus.FORBIDDEN);
	
	private final int code;
	private final String message;
	private final HttpStatusCode statusCode;
	
	ErrorCode(int code, String message, HttpStatusCode statusCode) {
		this.code = code;
		this.message = message;
		this.statusCode = statusCode;
	}
	
	
}
