package com.periodic.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.periodic.backend.security.JwtTokenUtils;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.converter.DefaultContentTypeResolver;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.security.core.Authentication;
import org.springframework.security.messaging.context.AuthenticationPrincipalArgumentResolver;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	@Value("${spring.data.redis.host}")
	private String host;
	@Value("${spring.data.redis.port}")
	private int port;
	@Value("${spring.data.redis.password}")
	private String password;
	
	@Autowired
	private JwtTokenUtils jwtTokenUtils;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
               .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app");
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(1);
        taskScheduler.setThreadNamePrefix("wss-heartbeat-thread-");
        taskScheduler.initialize();

        registry.enableSimpleBroker("/queue", "/topic")
                .setHeartbeatValue(new long[] {4000, 4000})
                .setTaskScheduler(taskScheduler);

        // Set user destination prefix cho tin nhắn riêng (vẫn cần thiết)
        registry.setUserDestinationPrefix("/user");
    }
    
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new AuthenticationPrincipalArgumentResolver());
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
    	registration.interceptors(new ChannelInterceptor() {
    		@Override
    		public Message<?> preSend(Message<?> message, MessageChannel channel) {
    		    System.out.println("Message: " + message);
    			try {
    		        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
    		        System.out.println("Accessor: " + accessor);
    		        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
    		            List<String> authHeaders = accessor.getNativeHeader("Authorization");
    		            if (authHeaders != null && !authHeaders.isEmpty()) {
    		                String raw = authHeaders.get(0);
    		                String token = raw.replace("Bearer ", "");

    		                Authentication user = jwtTokenUtils.getAuthentication(token);
    		                if (user != null && user.isAuthenticated()) {
    		                    accessor.setUser(user);
    		                    System.out.println("STOMP CONNECT – gán user: " + user.getName());
    		                }
    		            }
    		        }
    		    } catch (Exception e) {
    		        System.out.println("VL co loi");
    		        return null; // stop connection if error
    		    }
    		    return message;
    		}
    	});
    }

    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        // Configure outbound channel if needed
    }
    
    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        DefaultContentTypeResolver resolver = new DefaultContentTypeResolver();
        resolver.setDefaultMimeType(APPLICATION_JSON);
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setObjectMapper(new ObjectMapper());
        converter.setContentTypeResolver(resolver);
        messageConverters.add(converter);
        return false;
    }
} 