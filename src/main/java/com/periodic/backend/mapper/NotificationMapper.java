package com.periodic.backend.mapper;

import com.periodic.backend.domain.dto.notification.NotificationResponseDTO;
import com.periodic.backend.domain.entity.Notification;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    NotificationResponseDTO notificationToNotificationResponseDTO(Notification notification);

    default Page<NotificationResponseDTO> pageNotificationToPageNotificationResponseDTO(Page<Notification> page) {
        return page.map(this::notificationToNotificationResponseDTO);
    }
} 