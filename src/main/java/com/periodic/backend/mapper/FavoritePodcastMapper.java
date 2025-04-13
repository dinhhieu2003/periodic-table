package com.periodic.backend.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;

import com.periodic.backend.domain.entity.Element;
import com.periodic.backend.domain.entity.FavoritePodcast;
import com.periodic.backend.domain.entity.Podcast;
import com.periodic.backend.domain.response.favoritePodcast.FavoritePodcastResponse;
import com.periodic.backend.domain.response.favoritePodcast.FavoritePodcastShortResponse;

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
    
    default Page<FavoritePodcastShortResponse> pageFavoritePodcastToPageFavoritePodcastShortResponse(Page<FavoritePodcast> pageFavoritePodcast) {
        List<FavoritePodcastShortResponse> content = listFavoritePodcastToListFavoritePodcastShortResponse(pageFavoritePodcast.getContent());
        return new PageImpl<>(content, pageFavoritePodcast.getPageable(), pageFavoritePodcast.getTotalElements());
    }
    
    default List<FavoritePodcastShortResponse> listFavoritePodcastToListFavoritePodcastShortResponse(List<FavoritePodcast> favoritePodcasts) {
    	List<FavoritePodcastShortResponse> response = 
    			favoritePodcasts.stream()
    			.map(fav -> {
    				Podcast podcast = fav.getPodcast();
    				String title = podcast.getTitle();
    				String elementName = podcast.getElement().getName();
    				return new FavoritePodcastShortResponse(title, elementName,
    						podcast.getId());
    			})
    			.collect(Collectors.toList());
    	return response;
    }
} 