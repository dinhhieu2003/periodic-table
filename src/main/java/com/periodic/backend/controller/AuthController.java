package com.periodic.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.periodic.backend.domain.request.auth.LoginRequest;
import com.periodic.backend.domain.request.auth.RegisterRequest;
import com.periodic.backend.domain.request.auth.ResetPasswordRequest;
import com.periodic.backend.domain.request.auth.SendOTPRequest;
import com.periodic.backend.domain.request.auth.VerifyOTPRequest;
import com.periodic.backend.domain.response.auth.LoginResponse;
import com.periodic.backend.domain.response.auth.RegisterResponse;
import com.periodic.backend.domain.response.auth.ResetPasswordResponse;
import com.periodic.backend.domain.response.auth.SendOTPResponse;
import com.periodic.backend.domain.response.auth.VerifyOTPResponse;
import com.periodic.backend.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth API", description = "Authentication, authorization management")
public class AuthController {
	private final AuthService authService;
	@Operation(
		summary = "Login to the application",
		description = "Authenticate a user with email and password. Returns a JWT token if authentication is successful."
	)
	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
		LoginResponse loginResponse = authService.login(loginRequest);
		return ResponseEntity.ok(loginResponse);
	}
	@PostMapping("/register")
	public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
		return ResponseEntity.ok(authService.register(registerRequest));
	}
	@PostMapping("/verify")
	public ResponseEntity<VerifyOTPResponse> verifyOTP(@RequestBody VerifyOTPRequest verifyOTPRequest) {
		return ResponseEntity.ok(authService.verifyOTP(verifyOTPRequest));
	}
	@PostMapping("/verify-register")
	public ResponseEntity<VerifyOTPResponse> verifyOTPRegister(@RequestBody VerifyOTPRequest verifyOTPRequest) {
		return ResponseEntity.ok(authService.verifyOTPForRegister(verifyOTPRequest));
	}
	@PostMapping("/verify-change-email")
	public ResponseEntity<VerifyOTPResponse> verifyOTPChangeEmail(@RequestBody VerifyOTPRequest verifyOTPRequest) {
		return ResponseEntity.ok(authService.verifyOTPChangeEmail(verifyOTPRequest));
	}
	@PostMapping("/sendOTP")
	public ResponseEntity<SendOTPResponse> sendOTP(@RequestBody SendOTPRequest sendOTPRequest) {
		return ResponseEntity.ok(authService.sendOTP(sendOTPRequest));
	}
	@PostMapping("/reset-password")
	public ResponseEntity<ResetPasswordResponse> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
		return ResponseEntity.ok(authService.resetPassword(resetPasswordRequest));
	}
}
