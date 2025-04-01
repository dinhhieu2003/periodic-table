package com.periodic.backend.domain.response.podcast;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PodcastResponse {
	private Long id;
	private String title;
	private String audioUrl;
	private String transcript;
	// element name
	private String element;
	private boolean active;
}
