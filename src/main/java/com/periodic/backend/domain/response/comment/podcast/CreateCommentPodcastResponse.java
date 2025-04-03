package com.periodic.backend.domain.response.comment.podcast;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@SuperBuilder
public class CreateCommentPodcastResponse extends CommentPodcastResponse {
} 