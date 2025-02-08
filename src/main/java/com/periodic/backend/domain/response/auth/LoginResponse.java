package com.periodic.backend.domain.response.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
	private long id;
	private String email;
	private String name;
	private String accessToken;
	private String role;
	private boolean isActive;
}
