package com.periodic.backend.domain.response.comment.podcast;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ToggleActiveCommentPodcastResponse {
    private Long id;
    private boolean active;
} 