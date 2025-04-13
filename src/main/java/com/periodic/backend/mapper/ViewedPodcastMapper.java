package com.periodic.backend.mapper;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;

import com.periodic.backend.domain.entity.Podcast;
import com.periodic.backend.domain.entity.ViewedPodcast;
import com.periodic.backend.domain.response.viewedPodcast.ViewedPodcastResponse;
import com.periodic.backend.domain.response.viewedPodcast.ViewedPodcastShortResponse;

@Component
@Mapper(componentModel = "spring")
public interface ViewedPodcastMapper {
    ViewedPodcastResponse viewedPodcastToViewedPodcastResponse(ViewedPodcast viewedPodcast);
    
    default Page<ViewedPodcastResponse> pageViewedPodcastToPageViewedPodcastResponse(Page<ViewedPodcast> pageViewedPodcast) {
        List<ViewedPodcastResponse> content = pageViewedPodcast.getContent()
                .stream()
                .map(this::viewedPodcastToViewedPodcastResponse)
                .collect(Collectors.toList());
        return new PageImpl<>(content, pageViewedPodcast.getPageable(), pageViewedPodcast.getTotalElements());
    }
    
    default Page<ViewedPodcastShortResponse> pageViewedPodcastToPageViewedPodcastShortResponse(Page<ViewedPodcast> pageViewedPodcast) {
        List<ViewedPodcastShortResponse> content = listViewedPodcastToListViewedPodcastShortResponse(pageViewedPodcast.getContent());
        return new PageImpl<>(content, pageViewedPodcast.getPageable(), pageViewedPodcast.getTotalElements());
    } 
    
    default List<ViewedPodcastShortResponse> listViewedPodcastToListViewedPodcastShortResponse(List<ViewedPodcast> viewedPodcasts) {
    	List<ViewedPodcastShortResponse> response = 
    			viewedPodcasts.stream()
    			.map(vw -> {
    				Podcast podcast = vw.getPodcast();
    				String title = podcast.getTitle();
    				String elementName = podcast.getElement().getName();
    				Instant lastSeen = vw.getLastSeen();
    				return new ViewedPodcastShortResponse(title, elementName, lastSeen,
    						podcast.getId());
    			})
    			.collect(Collectors.toList());
    	return response;
    }
} 