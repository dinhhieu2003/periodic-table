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
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.periodic.backend.security.JwtTokenUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.security.Principal;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.converter.DefaultContentTypeResolver;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.ServerHttpRequest;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Configuration
@EnableWebSocketMessageBroker
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {	
	
	@Autowired
	private JwtTokenUtils jwtTokenUtils;
	@Autowired
	private PrincipalHandshakeInterceptor principalHandshakeInterceptor;
	@Autowired
	private CustomHandshakeHandler customHandshakeHandler;
	private static Set<String> connectedSessionIds = Collections.synchronizedSet(new HashSet<>());
	
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
//                .addInterceptors(principalHandshakeInterceptor)
//                .setHandshakeHandler(new DefaultHandshakeHandler() {
//                	@Override
//                    protected Principal determineUser(ServerHttpRequest request,
//                                                      WebSocketHandler wsHandler,
//                                                      Map<String, Object> attributes) {
//                        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//                        System.out.println("Authentication: " + authentication);
//                        List<String> auth = request.getHeaders().get("Authorization");
//                        System.out.println("Auth header: " + auth);
//                        if (authentication != null && authentication.isAuthenticated()) {
//                            return authentication;
//                        }
//                        return new Principal() {
//                            @Override
//                            public String getName() {
//                                return "anonymous_" + UUID.randomUUID();
//                            }
//                        };
//                    }
//                })
               .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app");

        registry.enableSimpleBroker("/queue", "/topic");
        
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
    	
    	registration.interceptors(new ChannelInterceptor() {
    		@Override
    		public Message<?> preSend(Message<?> message, MessageChannel channel) {
    		    System.out.println("Message: " + message);
		        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
		        System.out.println("Accessor: " + accessor);
		        var sessionId = accessor.getSessionId();
		        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
		        	connectedSessionIds.add(sessionId);
		        	List<String> authHeaders = accessor.getNativeHeader("Authorization");
		            System.out.println("Auth header inbound: " + authHeaders);
		            if (authHeaders != null && !authHeaders.isEmpty()) {
		                String raw = authHeaders.get(0);
		                String token = raw.replace("Bearer ", "");

		                Authentication user = jwtTokenUtils.getAuthentication(token);
		                if (user != null && user.isAuthenticated()) {
		                    accessor.setUser(user);
		                    System.out.println("STOMP CONNECT – gán user: " + accessor.getUser());
		                }
		            }
		        }
		        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
                    String destination = accessor.getDestination();
                    log.info("Client subscribed to topic: {}", destination);
                    log.info("User: " + accessor.getUser());
                } else if (StompCommand.UNSUBSCRIBE.equals(accessor.getCommand())) {
                    String destination = accessor.getDestination();
                    log.info("Client unsubscribed from topic: {}", destination);
                }
    		    return message;
    		}
    	});
    }
//
    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
                if (StompCommand.CONNECTED.equals(accessor.getCommand())) {
                    System.out.println("--- Server sending CONNECT frame ---");
                    System.out.println("Headers: " + accessor.toMap()); // In ra tất cả các header
                    System.out.println("User-Name header: " + accessor.getFirstNativeHeader("user-name")); // Kiểm tra cụ thể user-name
                    System.out.println("------------------------------------");
                }
                return message;
            }
        });
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