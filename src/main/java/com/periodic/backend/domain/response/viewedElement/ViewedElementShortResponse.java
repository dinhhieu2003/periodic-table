package com.periodic.backend.domain.response.viewedElement;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ViewedElementShortResponse {
	private String elementName;
	private String symbol;
	private String image;
	private Instant lastSeen;
	private Long elementId;
}
