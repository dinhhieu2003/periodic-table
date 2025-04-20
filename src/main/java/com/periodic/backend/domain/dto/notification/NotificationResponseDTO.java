package com.periodic.backend.domain.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponseDTO {
    private Long id;
    private String type;
    private String message;
    private Long relatedId;
    private boolean read;
    private Instant createdAt;
} 