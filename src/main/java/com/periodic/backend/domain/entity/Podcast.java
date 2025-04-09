package com.periodic.backend.domain.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
	@JsonIgnore
	private Element element;
	
	@OneToMany(mappedBy = "podcast")
	@JsonIgnore
	private List<CommentPodcast> comments = new ArrayList<>();
	
	@OneToMany(mappedBy =  "podcast")
	@JsonIgnore
	private List<FavoritePodcast> podcasts = new ArrayList<>();
	
	@OneToMany(mappedBy = "podcast")
	@JsonIgnore
	private List<ViewedPodcast> viewedPodcasts = new ArrayList<>();
}
