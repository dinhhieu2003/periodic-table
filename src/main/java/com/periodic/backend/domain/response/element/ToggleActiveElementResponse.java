package com.periodic.backend.domain.response.element;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ToggleActiveElementResponse {
	private Long id;
	private boolean active;
}
