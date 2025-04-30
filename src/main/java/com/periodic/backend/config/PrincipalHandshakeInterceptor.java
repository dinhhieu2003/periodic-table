package com.periodic.backend.config;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.periodic.backend.security.JwtTokenUtils;

@Component
public class PrincipalHandshakeInterceptor implements HandshakeInterceptor {
	@Autowired
	private JwtTokenUtils jwtTokenUtils;
	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Map<String, Object> attributes) throws Exception {
		List<String> auth = request.getHeaders().get("Authorization");
		System.out.println("Auth ne:" + request.getHeaders());
	    if (auth != null && !auth.isEmpty()) {
	      String token = auth.get(0).replace("Bearer ", "");
	      Authentication user = jwtTokenUtils.getAuthentication(token);
	      System.out.println("Principal: " + user);
	      if (user != null && user.isAuthenticated()) {
	        // Lưu vào attributes để HandshakeHandler set thành Principal
	    	  
	    	  attributes.put("principal", user);
	      }
	    }
	    return true;
	}

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Exception exception) {
		// TODO Auto-generated method stub
		
	}
	
}
