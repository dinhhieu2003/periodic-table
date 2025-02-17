package com.periodic.backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.periodic.backend.domain.entity.User;
import com.periodic.backend.domain.request.user.ChangePasswordRequest;
import com.periodic.backend.domain.response.user.ChangePasswordResponse;
import com.periodic.backend.exception.AppException;
import com.periodic.backend.repository.UserRepository;
import com.periodic.backend.security.SecurityUtils;
import com.periodic.backend.util.constant.ErrorCode;
import com.periodic.backend.util.constant.Status;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	
	public void updateRefreshToken(User user, String refreshToken) {
		user.setRefreshToken(refreshToken);
		userRepository.save(user);
	}
	
	public User saveUser(User user) {
		return userRepository.save(user);
	}
	
	public List<User> getAllUser() {
		List<User> listUser = this.userRepository.findAll();
		return listUser;
	}
	
	public User getUserByEmail(String email) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
		return user;
	}
	
	public boolean userIsExisted(String email) {
		Optional<User> userOption = userRepository.findByEmail(email);
		return userOption.isPresent();
	}
	
	public void deleteUser(String email) {
		User user = getUserByEmail(email);
		userRepository.delete(user);
	}
	
	public ChangePasswordResponse changePassword(ChangePasswordRequest changePasswordRequest) {
		String email = SecurityUtils.getCurrentUserLogin()
				.orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));
		User user = getUserByEmail(email);
		String password = changePasswordRequest.getPassword();
		user.setPassword(password);
		saveUser(user);
		return ChangePasswordResponse.builder().email(email).build();
	}
}
