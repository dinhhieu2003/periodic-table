package com.periodic.backend.repository;

import com.periodic.backend.domain.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long>, JpaSpecificationExecutor<Notification> {

    /**
     * Finds notifications for a specific user, ordered by creation date descending.
     *
     * @param userId   The ID of the user.
     * @param pageable Pagination information.
     * @return A page of notifications.
     */
    Page<Notification> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    /**
     * Finds all unread notifications for a specific user.
     *
     * @param userId The ID of the user.
     * @return A list of unread notifications.
     */
    List<Notification> findByUserIdAndIsReadFalse(Long userId);
} 