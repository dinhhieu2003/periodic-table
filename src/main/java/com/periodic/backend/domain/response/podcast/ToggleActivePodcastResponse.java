package com.periodic.backend.domain.response.podcast;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ToggleActivePodcastResponse {
	private Long id;
	private boolean active;
}
