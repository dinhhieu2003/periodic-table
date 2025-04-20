package com.periodic.backend.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "notifications")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String type;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;

    @Column
    private Long relatedId;

    @Column(nullable = false)
    private boolean isRead; // Default value
} 