package com.periodic.backend.config;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

@Component
public class CustomHandshakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        Authentication authentication = (Authentication) attributes.get("principal");
        System.out.println(authentication);
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication;
        }
        
        return new Principal() {
            @Override
            public String getName() {
                return "anonymous_" + UUID.randomUUID();
            }
        };
    }
}