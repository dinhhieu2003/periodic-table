package com.periodic.backend.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.periodic.backend.exception.AppException;
import com.periodic.backend.repository.UserRepository;
import com.periodic.backend.util.constant.ErrorCode;

import lombok.RequiredArgsConstructor;

@Component("userDetailsService")
@RequiredArgsConstructor
public class DomainUserDetailsService implements UserDetailsService {

	private static final Logger LOG = LoggerFactory.getLogger(DomainUserDetailsService.class);
	private final UserRepository userRepository;
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		LOG.debug("Authenticating {}", username);
		System.out.println("Hello eve");
		return userRepository.findByEmail(username)
				.orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
		
	}

}
