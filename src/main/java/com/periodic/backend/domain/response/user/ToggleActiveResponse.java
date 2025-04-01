package com.periodic.backend.domain.response.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ToggleActiveResponse {
	private long id;
	private boolean isActive;
}
