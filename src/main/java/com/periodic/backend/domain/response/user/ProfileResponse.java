package com.periodic.backend.domain.response.user;

import java.util.ArrayList;
import java.util.List;

import com.periodic.backend.domain.entity.FavoriteElement;
import com.periodic.backend.domain.entity.FavoritePodcast;
import com.periodic.backend.domain.entity.ViewedElement;
import com.periodic.backend.domain.entity.ViewedPodcast;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
public class ProfileResponse extends UserResponse {
	private List<FavoriteElement> favoriteElements = new ArrayList<>();
	private List<ViewedElement> viewedElements = new ArrayList<>();
	private List<FavoritePodcast> favoritePodcasts = new ArrayList<>();
	private List<ViewedPodcast> viewedPodcasts = new ArrayList<>();
}
