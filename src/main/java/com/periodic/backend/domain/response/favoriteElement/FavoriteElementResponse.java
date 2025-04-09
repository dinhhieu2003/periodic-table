package com.periodic.backend.domain.response.favoriteElement;

import com.periodic.backend.domain.entity.Element;
import com.periodic.backend.domain.entity.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class FavoriteElementResponse {
	private Element element;
	private boolean active;
}
