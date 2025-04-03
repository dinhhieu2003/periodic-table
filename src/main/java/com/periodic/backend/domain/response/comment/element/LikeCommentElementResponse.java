package com.periodic.backend.domain.response.comment.element;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeCommentElementResponse {
    private Long id;
    private int likes;
} 