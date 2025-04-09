package com.periodic.backend.domain.response.favoritePodcast;

import com.periodic.backend.domain.entity.Podcast;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class FavoritePodcastResponse {
    private Podcast podcast;
    private boolean active;
} 