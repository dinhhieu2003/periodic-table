package com.periodic.backend.service;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import com.periodic.backend.security.SecurityUtils;
import com.periodic.backend.util.PasswordGenerator;
import com.periodic.backend.util.constant.ErrorCode;
import com.periodic.backend.util.constant.Role;
import com.periodic.backend.util.constant.Status;
import com.periodic.backend.util.constant.VerifyOTPStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
	private final JwtTokenUtils jwtTokenUtils;
	private final UserService userService;
	private final OtpService otpService;
	private final EmailService emailService;
	private final PasswordEncoder passwordEncoder;
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
				.avatar(currentUser.getAvatar())
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
		String passwordHashed = passwordEncoder.encode(password);
		User newUser = User.builder()
				.email(email)
				.password(passwordHashed)
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
	
	public VerifyOTPResponse verifyOTPForRegister(VerifyOTPRequest verifyOTPRequest) {
		String email = verifyOTPRequest.getEmail();
		String otp = verifyOTPRequest.getOtp();
		
		// Verify OTP
		if(!otpService.verifyOTP(email, otp)) {
			log.error("OTP that user sent was not found in Redis");
			throw new AppException(ErrorCode.OTP_NOT_FOUND);
		}
		
		// Set status VERIFIED for user
		User user = userService.getUserByEmail(email);
		user.setStatus(Status.VERIFIED);
		user.setActive(true);
		userService.saveUser(user);
		
		VerifyOTPResponse verifyOTPResponse = new VerifyOTPResponse();
		verifyOTPResponse.setEmail(email);
		verifyOTPResponse.setVerifyStatus(VerifyOTPStatus.SUCCESS.name());

		otpService.deleteOTP(email);
		
		return verifyOTPResponse;
	}
	
	public VerifyOTPResponse verifyOTPChangeEmail(VerifyOTPRequest verifyOTPRequest) {
		String newEmail = verifyOTPRequest.getEmail();
		String otp = verifyOTPRequest.getOtp();
		
		if(userService.userIsExisted(newEmail)) {
			throw new AppException(ErrorCode.USER_ALREADY_EXISTS);
		}
		
		// Verify OTP
		if(!otpService.verifyOTP(newEmail, otp)) {
			log.error("OTP that user sent was not found in Redis");
			throw new AppException(ErrorCode.OTP_NOT_FOUND);
		}
		
		// Change email for current user
		String email = SecurityUtils.getCurrentUserLogin()
				.orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));
		User currentUser = userService.getUserByEmail(email);
		currentUser.setEmail(newEmail);
		userService.saveUser(currentUser);
		
		VerifyOTPResponse verifyOTPResponse = new VerifyOTPResponse();
		verifyOTPResponse.setEmail(email);
		verifyOTPResponse.setVerifyStatus(VerifyOTPStatus.SUCCESS.name());

		otpService.deleteOTP(email);
		
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
		boolean isMultipart = false;
		boolean isHtml = true;
		emailService.sendEmailSync(email, "VERIFY YOUR EMAIL", 
				contentHtml, isMultipart, isHtml);
	}
	
	// call api send otp
	// verify otp -> set new password
	public ResetPasswordResponse resetPassword(ResetPasswordRequest resetPasswordRequest) {
		// Step 1: Ensure password and confirm password are valid match
		String password = resetPasswordRequest.getPassword();
		String passwordConfirm = resetPasswordRequest.getPasswordConfirm();
		if(!password.equals(passwordConfirm)) {
			throw new AppException(ErrorCode.PASSWORDCONFIRM_NOT_MATCH);
		}
		// Step 2: Verify OTP
		String email = resetPasswordRequest.getEmail();
		String otp = resetPasswordRequest.getOtp();
		if(!otpService.verifyOTP(email, otp)) {
			log.error("OTP that user sent was not found in Redis");
			throw new AppException(ErrorCode.OTP_NOT_FOUND);
		}
		// Set new password for user
		User user = userService.getUserByEmail(email);
		String passwordHashed = passwordEncoder.encode(password);
		user.setPassword(passwordHashed);
		userService.saveUser(user);
		
		otpService.deleteOTP(email);
		
		// Step 3: Build response
		return ResetPasswordResponse.builder().email(email)
				.verifyStatus(VerifyOTPStatus.SUCCESS.name()).build();
	}
	
	public VerifyOTPResponse verifyOTP(VerifyOTPRequest verifyOTPRequest) {
		String email = verifyOTPRequest.getEmail();
		String otp = verifyOTPRequest.getOtp();
		
		// Verify OTP
		if(!otpService.verifyOTP(email, otp)) {
			log.error("OTP that user sent was not found in Redis");
			throw new AppException(ErrorCode.OTP_NOT_FOUND);
		}
		
		VerifyOTPResponse verifyOTPResponse = new VerifyOTPResponse();
		verifyOTPResponse.setEmail(email);
		verifyOTPResponse.setVerifyStatus(VerifyOTPStatus.SUCCESS.name());
		
		return verifyOTPResponse;
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
