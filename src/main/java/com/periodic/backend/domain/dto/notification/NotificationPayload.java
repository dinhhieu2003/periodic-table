package com.periodic.backend.domain.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPayload {
    private String type;
    private String message;
    private Long relatedId;
    private String timestamp; // ISO 8601 format
} 