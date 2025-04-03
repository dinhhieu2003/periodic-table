package com.periodic.backend.domain.response.comment.element;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ToggleActiveCommentElementResponse {
    private Long id;
    private boolean active;
} 