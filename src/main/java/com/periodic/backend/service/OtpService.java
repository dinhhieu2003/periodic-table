package com.periodic.backend.service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OtpService {
	private final RedisTemplate<String, String> redisTemplate;
	private final long OTP_EXPIRE_SECONDS = 300; // 5 minutes
	
	public String generateOTP(String email) {
		// Step 1: Random a 6 digits number (from 100000 to 999999)
		// Random().nextInt(max - min + 1) + min
		String otp = String.valueOf(new Random().nextInt(900000) + 100000);
        // Step 2: Save OTP to Redis
		redisTemplate.opsForValue().set(email, otp, OTP_EXPIRE_SECONDS, TimeUnit.SECONDS);
        return otp;
	}
	
	public boolean verifyOTP(String email, String inputOtp) {
		String storedOtp = redisTemplate.opsForValue().get(email);
        return storedOtp != null && storedOtp.equals(inputOtp);
	}
	
	public void deleteOTP(String email) {
		String key = email;
		redisTemplate.delete(key);
	}
}
