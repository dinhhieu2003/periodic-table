package com.periodic.backend.domain.request.podcast;

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
public class PodcastRequest {
	@NotBlank(message = "Title is required")
	private String title;
	@NotBlank(message = "Audio url is required")
	private String audioUrl;
	private String transcript;
	@NotNull(message = "Element is required")
	private Long elementId;
}
