package com.periodic.backend.domain.request.user;

import com.periodic.backend.util.constant.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRoleRequest {
	private Role role;
}
