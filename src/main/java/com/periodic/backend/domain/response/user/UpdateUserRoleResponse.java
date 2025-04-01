package com.periodic.backend.domain.response.user;

import com.periodic.backend.util.constant.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRoleResponse {
	private long id;
	private Role role;
}
