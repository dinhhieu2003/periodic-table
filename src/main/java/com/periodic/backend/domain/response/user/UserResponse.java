package com.periodic.backend.domain.response.user;

import com.periodic.backend.util.constant.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UserResponse {
	private Long id;
	private String email;
	private String name;
	private String avatar;
	private Role role;
}
