package com.periodic.backend.domain.response.viewedPodcast;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ViewedPodcastShortResponse {
	private String title;
	private String elementName;
	private Instant lastSeen;
	private Long podcastId;
}
