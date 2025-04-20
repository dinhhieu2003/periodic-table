package com.periodic.backend.service;

import com.periodic.backend.domain.dto.notification.NotificationResponseDTO;
import com.periodic.backend.domain.entity.Notification;
import com.periodic.backend.domain.entity.User;
import com.periodic.backend.exception.AppException;
import com.periodic.backend.mapper.NotificationMapper;
import com.periodic.backend.repository.NotificationRepository;
import com.periodic.backend.repository.UserRepository;
import com.periodic.backend.util.constant.ErrorCode;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;

    @Transactional
    public Notification createAndSaveNotification(Long userId, String type, String message, Long relatedId) {
        log.info("Attempting to save notification for user {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        Notification notification = Notification.builder()
                .user(user)
                .type(type)
                .message(message)
                .relatedId(relatedId)
                .isRead(false)
                .build();
        // Note: BaseEntity handles createdAt/updatedAt automatically
        notification.setActive(true); // Ensure notification is active

        Notification savedNotification = notificationRepository.save(notification);
        log.info("Successfully saved notification {} for user {}", savedNotification.getId(), userId);
        return savedNotification;
    }

    @Transactional(readOnly = true)
    public Page<NotificationResponseDTO> getNotificationsForUser(Long userId, Pageable pageable) {
        log.info("Fetching notifications for user {} with pageable {}", userId, pageable);
        Page<Notification> notificationPage = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        log.info("Found {} notifications for user {}", notificationPage.getTotalElements(), userId);
        return notificationMapper.pageNotificationToPageNotificationResponseDTO(notificationPage);
    }

    @Transactional
    public NotificationResponseDTO markNotificationAsRead(Long notificationId, Long userId) {
        log.info("Attempting to mark notification {} as read for user {}", notificationId, userId);
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_FOUND));

        if (!Objects.equals(notification.getUser().getId(), userId)) {
            log.warn("User {} attempted to mark notification {} belonging to user {} as read. Unauthorized.",
                     userId, notificationId, notification.getUser().getId());
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        if (notification.isRead()) {
            log.info("Notification {} was already marked as read.", notificationId);
        } else {
            notification.setRead(true);
            notification = notificationRepository.save(notification);
            log.info("Successfully marked notification {} as read for user {}", notificationId, userId);
        }
        return notificationMapper.notificationToNotificationResponseDTO(notification);
    }

    @Transactional
    public long markAllNotificationsAsRead(Long userId) {
        log.info("Attempting to mark all unread notifications as read for user {}", userId);
        List<Notification> unreadNotifications = notificationRepository.findByUserIdAndIsReadFalse(userId);

        if (unreadNotifications.isEmpty()) {
            log.info("No unread notifications found for user {}.", userId);
            return 0;
        }

        unreadNotifications.forEach(notification -> notification.setRead(true));
        
        // Use saveAll for potential batching optimization by the JPA provider
        notificationRepository.saveAll(unreadNotifications);
        
        long count = unreadNotifications.size();
        log.info("Successfully marked {} notifications as read for user {}", count, userId);
        return count;
    }

    @Transactional
    public void deleteNotification(Long notificationId, Long userId) {
        log.info("Attempting to delete notification {} for user {}", notificationId, userId);
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_FOUND));

        if (!Objects.equals(notification.getUser().getId(), userId)) {
             log.warn("User {} attempted to delete notification {} belonging to user {} as read. Unauthorized.",
                     userId, notificationId, notification.getUser().getId());
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        notificationRepository.delete(notification); // Use delete(entity) instead of deleteById to ensure cascade works if needed
        log.info("Successfully deleted notification {} for user {}", notificationId, userId);
    }
} 