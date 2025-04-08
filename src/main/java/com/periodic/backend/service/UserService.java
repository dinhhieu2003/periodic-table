package com.periodic.backend.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.periodic.backend.domain.entity.User;
import com.periodic.backend.domain.request.user.ChangePasswordRequest;
import com.periodic.backend.domain.request.user.UpdateUserRoleRequest;
import com.periodic.backend.domain.response.pagination.PaginationResponse;
import com.periodic.backend.domain.response.user.ChangePasswordResponse;
import com.periodic.backend.domain.response.user.GetUserResponse;
import com.periodic.backend.domain.response.user.ToggleActiveResponse;
import com.periodic.backend.domain.response.user.UpdateUserResponse;
import com.periodic.backend.domain.response.user.UpdateUserRoleResponse;
import com.periodic.backend.exception.AppException;
import com.periodic.backend.mapper.UserMapper;
import com.periodic.backend.repository.UserRepository;
import com.periodic.backend.repository.specification.UserSpecification;
import com.periodic.backend.security.SecurityUtils;
import com.periodic.backend.util.PaginationUtils;
import com.periodic.backend.util.constant.ErrorCode;
import com.periodic.backend.util.constant.Role;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final Logger log = LoggerFactory.getLogger(UserService.class);
	private final UserMapper userMapper;
	
	public void updateRefreshToken(User user, String refreshToken) {
		user.setRefreshToken(refreshToken);
		userRepository.save(user);
	}
	
	public User saveUser(User user) {
		return userRepository.save(user);
	}
	
	public UpdateUserResponse updateUser(User user) {
		User updatedUser = userRepository.save(user);
		UpdateUserResponse response = userMapper.userToUpdateUserResponse(updatedUser);
		return response;
	}
	
	public PaginationResponse<List<GetUserResponse>> getUsers(Pageable pageable, String term, String[] sortBy, String[] sortDirection, Boolean active) {
		log.info("Start: Get users with search term: {}, sort by: {}, sort direction: {}, and active: {}", 
				term, sortBy != null ? String.join(",", sortBy) : null, 
				sortDirection != null ? String.join(",", sortDirection) : null, active);
		
		UserSpecification specification = new UserSpecification(term, sortBy, sortDirection, active);
		Page<User> pageUser = userRepository.findAll(specification, pageable);
		
		Page<GetUserResponse> pageData = userMapper.pageUserToPageGetUserResponse(pageUser);
		PaginationResponse<List<GetUserResponse>> response = 
				PaginationUtils.buildPaginationResponse(pageable, pageData);
		
		log.info("End: Get users success");
		return response;
	}
	
	public GetUserResponse getUser(Long id) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
		GetUserResponse response = userMapper.userToGetUserResponse(user);
		return response;
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
	
	public UpdateUserRoleResponse updateRole(Long userId, UpdateUserRoleRequest updateUserRoleRequest) {
		log.info("Start: Function update role for user id {}", userId);
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
		Role role = updateUserRoleRequest.getRole();
		user.setRole(role);
		User updatedUser = userRepository.save(user);
		log.info("Update user role in database success with user id {}", user.getId());
		UpdateUserRoleResponse response = userMapper.userToUpdateUserRoleResponse(updatedUser);
		log.info("End: Function update role for user id {} success", updatedUser.getId());
		return response;
	}
	
	public ToggleActiveResponse toggleActive(Long userId) {
		log.info("Start: Function toggle active for user id {}", userId);
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
		boolean active = !user.isActive();
		user.setActive(active);
		User updatedUser = userRepository.save(user);
		log.info("Update user active into database success");
		ToggleActiveResponse response = userMapper.userToToggleActiveResponse(updatedUser);
		return response;
	}
}
