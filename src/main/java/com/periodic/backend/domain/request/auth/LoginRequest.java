package com.periodic.backend.domain.request.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
	@NotBlank(message="Email cannot be empty")
	private String email;
	@NotBlank(message="Password cannot be empty")
	@Size(min=6, message="Password must be at least 6 characters")
	private String password;
}
