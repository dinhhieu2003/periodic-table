package com.periodic.backend.domain.request.comment.element;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import jakarta.validation.constraints.NotBlank;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@SuperBuilder
public class UpdateCommentElementRequest {
    @NotBlank(message = "Content is required")
    private String content;
} 