package com.periodic.backend.service;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.periodic.backend.domain.entity.User;
import com.periodic.backend.domain.request.auth.LoginRequest;
import com.periodic.backend.domain.request.auth.RegisterRequest;
import com.periodic.backend.domain.request.auth.ResetPasswordRequest;
import com.periodic.backend.domain.request.auth.SendOTPRequest;
import com.periodic.backend.domain.request.auth.VerifyOTPRequest;
import com.periodic.backend.domain.response.auth.LoginResponse;
import com.periodic.backend.domain.response.auth.RegisterResponse;
import com.periodic.backend.domain.response.auth.ResetPasswordResponse;
import com.periodic.backend.domain.response.auth.SendOTPResponse;
import com.periodic.backend.domain.response.auth.Tokens;
import com.periodic.backend.domain.response.auth.VerifyOTPResponse;
import com.periodic.backend.exception.AppException;
import com.periodic.backend.security.JwtTokenUtils;
import com.periodic.backend.util.PasswordGenerator;
import com.periodic.backend.util.constant.ErrorCode;
import com.periodic.backend.util.constant.Role;
import com.periodic.backend.util.constant.Status;
import com.periodic.backend.util.constant.VerifyOTPStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
	private final JwtTokenUtils jwtTokenUtils;
	private final UserService userService;
	private final OtpService otpService;
	private final EmailService emailService;
	private final PasswordGenerator passwordGenerator;
	private final AuthenticationManagerBuilder authenticationManagerBuilder;
	
	public LoginResponse login(LoginRequest loginRequest) {
		// Step 1: Save authentication token into security context
		UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = 
				new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());
		Authentication authentication = authenticationManagerBuilder.getObject().authenticate(usernamePasswordAuthenticationToken);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		// Step 2: Get current user
		String currentEmail = loginRequest.getEmail();
		User currentUser = userService.getUserByEmail(currentEmail);
		// Step 4: Create accessToken and refreshToken
		String accessToken = jwtTokenUtils.createAccessToken(currentUser);
		String refreshToken = jwtTokenUtils.createRefreshToken(currentUser);
		// Step 5: Update refreshToken for user 
		userService.updateRefreshToken(currentUser, refreshToken);
		LoginResponse loginResponse = LoginResponse.builder()
				.id(currentUser.getId())
				.accessToken(accessToken)
				.email(currentEmail)
				.name(currentUser.getName())
				.isActive(currentUser.isActive())
				.role(currentUser.getRole().name())
				.build();
		return loginResponse;
	}
	
	public RegisterResponse register(RegisterRequest registerRequest) {
		// Step 1: Check email existed and VERIFIED
		// if email existed and verified -> throw USER_ALREADY_EXISTS
		// if email existed and pending -> delete current user, override register
		String email = registerRequest.getEmail();
		if(userService.userIsExisted(email)) {
			User userExisted = userService.getUserByEmail(email);
			if(userExisted.getStatus().equals(Status.VERIFIED)) {
				throw new AppException(ErrorCode.USER_ALREADY_EXISTS);
			} else {
				userService.deleteUser(email);
			}
		}
		// Step 2: Save new user with email and password hashed but pending
		String password = registerRequest.getPassword();
		User newUser = User.builder()
				.email(email)
				.password(password)
				.name(registerRequest.getName())
				.role(Role.USER)
				.status(Status.PENDING_VERIFICATION)
				.build();
		User userSaved = userService.saveUser(newUser);
		
		// Step 3: Send OTP to email
		sendOTPToEmail(email);
		
		// Step 4: Build and return registerResponse
		RegisterResponse registerResponse = RegisterResponse.builder()
				.name(userSaved.getName())
				.email(userSaved.getEmail())
				.status(userSaved.getStatus().name())
				.build();
		return registerResponse;
	}
	
	public VerifyOTPResponse verifyOTP(VerifyOTPRequest verifyOTPRequest) {
		// Step 1: Get email and otp from request to verify
		String email = verifyOTPRequest.getEmail();
		String otp = verifyOTPRequest.getOtp();
		// Set default verifyStatus is FAILED
		String verifyStatus = VerifyOTPStatus.FAILED.name();
		
		// Step 2: Build VerifyOTPResponse
		VerifyOTPResponse verifyOTPResponse = new VerifyOTPResponse();
		verifyOTPResponse.setEmail(email);
		verifyOTPResponse.setVerifyStatus(verifyStatus);
		// Verify OTP
		if(otpService.verifyOTP(email, otp)) {
			verifyStatus = VerifyOTPStatus.SUCCESS.name();
			verifyOTPResponse.setVerifyStatus(verifyStatus);
			// Set status for user
			userService.updateStatusUser(email, Status.VERIFIED);
			userService.updateActiveUser(email, true);
		}
		return verifyOTPResponse;
	}
	
	public SendOTPResponse sendOTP(SendOTPRequest sendOTPRequest) {
		String email = sendOTPRequest.getEmail();
		sendOTPToEmail(email);
		SendOTPResponse sendOTPResponse = SendOTPResponse.builder()
				.email(email).build();
		return sendOTPResponse;
	}
	
	private void sendOTPToEmail(String email) {
		String otp = otpService.generateOTP(email);
		String contentHtml = "<h1>Your OTP is: </h1> " + "<b>"+ otp + "</b>";
		emailService.sendEmailSync(email, "VERIFY YOUR EMAIL", 
				contentHtml, false, true);
	}
	
	public ResetPasswordResponse resetPassword(ResetPasswordRequest resetPasswordRequest) {
		// Step 1: Generate new password
		String newPassword = passwordGenerator.generateNewPassword();
		// Step 2: Set new password for user
		User user = userService.getUserByEmail(resetPasswordRequest.getEmail());
		user.setPassword(newPassword);
		userService.saveUser(user);
		// Step 3: Send new password to email
		String email = resetPasswordRequest.getEmail();
		String contentHtml = "<h1>Your new password is: </h1> " + "<b>"+ newPassword + "</b>";
		emailService.sendEmailSync(email, "NEW PASSWORD", 
				contentHtml, false, true);
		// Step 4: Build response
		return ResetPasswordResponse.builder().email(email).build();
	}
	
	public Tokens handleRefreshToken(User user) {
		String accessToken = jwtTokenUtils.createAccessToken(user);
		String refreshToken = jwtTokenUtils.createRefreshToken(user);
		userService.updateRefreshToken(user, refreshToken);
		return new Tokens(accessToken, refreshToken);
	}
	
	public String getCurrentEmail() {
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    String email = null;
	    if (authentication != null && authentication.isAuthenticated() 
	            && !(authentication instanceof AnonymousAuthenticationToken)) {
	    	email = authentication.getName();
	    }
	    return email;
	}
}
