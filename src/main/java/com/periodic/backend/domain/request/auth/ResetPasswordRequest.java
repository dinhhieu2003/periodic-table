package com.periodic.backend.domain.request.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResetPasswordRequest {
	private String email;
	private String password;
	private String passwordConfirm;
	private String otp;
}
