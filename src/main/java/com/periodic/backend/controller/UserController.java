package com.periodic.backend.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.periodic.backend.domain.entity.User;
import com.periodic.backend.domain.request.user.ChangePasswordRequest;
import com.periodic.backend.domain.request.user.UpdateUserRequest;
import com.periodic.backend.domain.request.user.UpdateUserRoleRequest;
import com.periodic.backend.domain.response.pagination.PaginationResponse;
import com.periodic.backend.domain.response.user.ChangePasswordResponse;
import com.periodic.backend.domain.response.user.ToggleActiveResponse;
import com.periodic.backend.domain.response.user.UpdateUserRoleResponse;
import com.periodic.backend.exception.AppException;
import com.periodic.backend.security.SecurityUtils;
import com.periodic.backend.service.UserService;
import com.periodic.backend.util.PaginationUtils;
import com.periodic.backend.util.constant.ErrorCode;
import com.periodic.backend.util.constant.PaginationParam;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
	private final UserService userService;
	private final Logger log = LoggerFactory.getLogger(UserController.class);
	
	@PostMapping("/change-password")
	public ResponseEntity<ChangePasswordResponse> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
		log.info("User is changing password");
		return ResponseEntity.ok(userService.changePassword(changePasswordRequest));
	}
	
	@Operation(summary = "Get users pageable")
	@GetMapping("")
	public ResponseEntity<PaginationResponse<List<User>>> getUsers(
			@RequestParam(defaultValue = PaginationParam.DEFAULT_CURRENT_PAGE) int current,
			@RequestParam(defaultValue = PaginationParam.DEFAULT_PAGE_SIZE) int pageSize,
			@RequestParam(required = false, defaultValue = "") String term) {
		Pageable pageable = PaginationUtils.createPageable(current, pageSize);
		return ResponseEntity.ok(userService.getUsers(pageable, term));
	}
	
	@Operation(summary = "Get one user by user id")
	@GetMapping("/{id}")
	public ResponseEntity<User> getUser(@PathVariable Long id) {
		return ResponseEntity.ok(userService.getUser(id));
	}
	
	@Operation(summary = "Update profile")
	@PutMapping("")
	public ResponseEntity<User> updateUser(@RequestBody UpdateUserRequest updateUser) {
		String email = SecurityUtils.getCurrentUserLogin()
				.orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));
		User user = userService.getUserByEmail(email);
		user.setName(updateUser.getName());
		user.setAvatar(updateUser.getAvatar());
		return ResponseEntity.ok(userService.saveUser(user));
	}
	
	@Operation(summary = "Update role for a user by user id")
	@PatchMapping("/{id}/roles")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<UpdateUserRoleResponse> updateRoleUser(@PathVariable Long id,
			@RequestBody UpdateUserRoleRequest updateUserRoleRequest) {
		log.info("Admin is changing role for user id {}", id);
		return ResponseEntity.ok(userService.updateRole(id, updateUserRoleRequest));
	}
	
	@Operation(summary = "Toggle active for a user by user id")
	@PatchMapping("/{id}/toggle-active")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ToggleActiveResponse> toggleActiveUser(@PathVariable Long id) {
		log.info("Admin is changing active for user id {}", id);
		return ResponseEntity.ok(userService.toggleActive(id));
	}
	
}
