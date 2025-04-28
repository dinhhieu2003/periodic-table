package com.periodic.backend.service;

import com.periodic.backend.domain.dto.notification.NotificationPayload;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationPublisher {

    private static final Logger log = LoggerFactory.getLogger(NotificationPublisher.class);
    private final SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    private SimpUserRegistry simpUserRegistry;
    public void publishNotificationToUser(String username, NotificationPayload payload) {
    	log.info("Active WebSocket users: {}", simpUserRegistry.getUsers().stream().map(SimpUser::getName).toList());
    	String userDestination = "/queue/notifications";

        try {
            log.info("Publishing notification type '{}' to user {} at destination {}", payload.getType(), username, userDestination);
            simpMessagingTemplate.convertAndSendToUser(username, userDestination, payload);
            log.info("Successfully published notification type '{}' to user {}", payload.getType(), username);
        } catch (Exception e) {
            log.error("Failed to publish notification type '{}' to user {}: {}", payload.getType(), username, e.getMessage(), e);
            // Depending on the application requirements, you might want to re-throw the exception,
            // implement retry logic, or notify an administrator.
        }
    }
} 