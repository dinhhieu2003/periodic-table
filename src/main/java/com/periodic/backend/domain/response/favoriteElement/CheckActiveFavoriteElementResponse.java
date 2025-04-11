package com.periodic.backend.domain.response.favoriteElement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckActiveFavoriteElementResponse {
	private Long elementId;
	private boolean active;
}
