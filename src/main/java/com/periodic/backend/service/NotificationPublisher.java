package com.periodic.backend.service;

import com.periodic.backend.domain.dto.notification.NotificationPayload;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationPublisher {

    private static final Logger log = LoggerFactory.getLogger(NotificationPublisher.class);
    private final SimpMessagingTemplate simpMessagingTemplate;

    public void publishNotificationToUser(Long userId, NotificationPayload payload) {
        String userDestination = "/queue/notifications";
        String userIdString = String.valueOf(userId); // convertAndSendToUser expects the user destination as a String

        try {
            log.info("Publishing notification type '{}' to user {} at destination {}", payload.getType(), userIdString, userDestination);
            simpMessagingTemplate.convertAndSendToUser(userIdString, userDestination, payload);
            log.info("Successfully published notification type '{}' to user {}", payload.getType(), userIdString);
        } catch (Exception e) {
            log.error("Failed to publish notification type '{}' to user {}: {}", payload.getType(), userIdString, e.getMessage(), e);
            // Depending on the application requirements, you might want to re-throw the exception,
            // implement retry logic, or notify an administrator.
        }
    }
} 