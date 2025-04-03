package com.periodic.backend.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "comment_podcast")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentPodcast extends BaseEntity{
	@Column(columnDefinition = "TEXT")
	private String content;
	private int likes;
	@ManyToOne
	@JoinColumn(name = "podcast_id")
	private Podcast podcast;
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
}
