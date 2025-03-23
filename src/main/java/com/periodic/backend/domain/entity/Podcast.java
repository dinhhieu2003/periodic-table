package com.periodic.backend.domain.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "podcasts")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Podcast extends BaseEntity{
	private String title;
	private String audioUrl;
	@Column(columnDefinition = "TEXT")
	private String transcript;
	
	@ManyToOne
	@JoinColumn(name = "element_id")
	private Element element;
	
	@OneToMany(mappedBy = "podcast")
	private Set<CommentPodcast> comments = new HashSet<>();
	
	@OneToMany(mappedBy =  "podcast")
	private Set<FavoritePodcast> podcasts = new HashSet<>();
	
	@OneToMany(mappedBy = "podcast")
	private Set<LearnedPodcast> learnedPodcasts = new HashSet<>();
}
