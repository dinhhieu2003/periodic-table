package com.periodic.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.periodic.backend.domain.entity.User;
import com.periodic.backend.domain.request.user.ChangePasswordRequest;
import com.periodic.backend.domain.response.user.ChangePasswordResponse;
import com.periodic.backend.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
	private final UserService userService;
	
	@PostMapping("/change-password")
	public ResponseEntity<ChangePasswordResponse> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
		return ResponseEntity.ok(userService.changePassword(changePasswordRequest));
	}
	
	@GetMapping("")
	public ResponseEntity<List<User>> getUser() {
		return ResponseEntity.ok(userService.getAllUser());
	}
}
