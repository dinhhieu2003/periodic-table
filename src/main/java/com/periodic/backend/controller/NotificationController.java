package com.periodic.backend.controller;

import com.periodic.backend.domain.dto.notification.NotificationResponseDTO;
import com.periodic.backend.domain.entity.User;
import com.periodic.backend.domain.response.pagination.PaginationResponse;
import com.periodic.backend.exception.AppException;
import com.periodic.backend.security.SecurityUtils;
import com.periodic.backend.service.NotificationService;
import com.periodic.backend.service.UserService;
import com.periodic.backend.util.PaginationUtils;
import com.periodic.backend.util.constant.ErrorCode;
import com.periodic.backend.util.constant.PaginationParam;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    // Helper method to get current user ID
    private Long getCurrentUserId() {
    	var authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.getUserByEmail(username);
        if (user == null) {
             throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        return user.getId();
    }

    @GetMapping
    public ResponseEntity<PaginationResponse<List<NotificationResponseDTO>>> getNotifications(
    		@RequestParam(defaultValue = PaginationParam.DEFAULT_CURRENT_PAGE) int current,
			@RequestParam(defaultValue = PaginationParam.DEFAULT_PAGE_SIZE) int pageSize) {
        Long userId = getCurrentUserId();
        Pageable pageable = PaginationUtils.createPageable(current, pageSize);
        Page<NotificationResponseDTO> notificationPage = notificationService.getNotificationsForUser(userId, pageable);
        PaginationResponse<List<NotificationResponseDTO>> response = 
            PaginationUtils.buildPaginationResponse(pageable, notificationPage);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<NotificationResponseDTO> markAsRead(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        NotificationResponseDTO updatedNotification = notificationService.markNotificationAsRead(id, userId);
        return ResponseEntity.ok(updatedNotification);
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Map<String, Long>> markAllAsRead() {
        Long userId = getCurrentUserId();
        long count = notificationService.markAllNotificationsAsRead(userId);
        return ResponseEntity.ok(Collections.singletonMap("markedAsReadCount", count));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        notificationService.deleteNotification(id, userId);
        return ResponseEntity.noContent().build();
    }
} 