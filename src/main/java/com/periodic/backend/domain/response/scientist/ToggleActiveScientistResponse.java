package com.periodic.backend.domain.response.scientist;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ToggleActiveScientistResponse {
	private Long id;
	private boolean active;
}
