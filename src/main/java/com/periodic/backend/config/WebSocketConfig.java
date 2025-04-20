package com.periodic.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.StompBrokerRelayRegistration;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	@Value("${spring.data.redis.host}")
	private String host;
	@Value("${spring.data.redis.port}")
	private int port;
	@Value("${spring.data.redis.password}")
	private String password;
	
    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Set prefix for the application endpoints
        registry.setApplicationDestinationPrefixes("/app");

        // Configure the simple broker for destinations starting with /topic, /queue, and /user
        registry.enableSimpleBroker("/topic", "/queue", "/user");

        // Set user destination prefix for user-specific messages (vẫn cần thiết)
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // Configure inbound channel if needed
    }

    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        // Configure outbound channel if needed
    }
} 