package com.periodic.backend.domain.request.comment.element;

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
public class CommentElementRequest {
    @NotBlank(message = "Content is required")
    private String content;
    
    @NotNull(message = "Element ID is required")
    private Long elementId;
} 