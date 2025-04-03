package com.periodic.backend.domain.response.comment.element;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CommentElementResponse {
    private Long id;
    private String content;
    private int likes;
    private String elementName;
    private String userName;
    private String userAvatar;
    private Instant createdAt;
    private boolean active;
} 