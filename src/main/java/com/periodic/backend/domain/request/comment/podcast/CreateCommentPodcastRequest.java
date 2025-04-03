package com.periodic.backend.domain.request.comment.podcast;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@SuperBuilder
public class CreateCommentPodcastRequest extends CommentPodcastRequest {
} 