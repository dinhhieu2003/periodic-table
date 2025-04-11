package com.periodic.backend.domain.response.favoritePodcast;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ToggleActiveFavoritePodcastResponse {
	private Long podcastId;
	private boolean active;
}
