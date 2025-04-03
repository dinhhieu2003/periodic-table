package com.periodic.backend.domain.request.comment.podcast;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@SuperBuilder
public class UpdateCommentPodcastRequest {
    @NotBlank(message = "Content is required")
    private String content;
} 