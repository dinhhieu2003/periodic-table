package com.periodic.backend.domain.request.comment.podcast;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CommentPodcastRequest {
    @NotBlank(message = "Content is required")
    private String content;
    
    @NotNull(message = "Podcast ID is required")
    private Long podcastId;
} 