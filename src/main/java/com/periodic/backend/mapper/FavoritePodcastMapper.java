package com.periodic.backend.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;

import com.periodic.backend.domain.entity.FavoritePodcast;
import com.periodic.backend.domain.response.favoritePodcast.FavoritePodcastResponse;

@Component
@Mapper(componentModel = "spring")
public interface FavoritePodcastMapper {
    FavoritePodcastResponse favoritePodcastToFavoritePodcastResponse(FavoritePodcast favoritePodcast);
    
    default Page<FavoritePodcastResponse> pageFavoritePodcastToPageFavoritePodcastResponse(Page<FavoritePodcast> pageFavoritePodcast) {
        List<FavoritePodcastResponse> content = pageFavoritePodcast.getContent()
                .stream()
                .map(this::favoritePodcastToFavoritePodcastResponse)
                .collect(Collectors.toList());
        return new PageImpl<>(content, pageFavoritePodcast.getPageable(), pageFavoritePodcast.getTotalElements());
    }
} 