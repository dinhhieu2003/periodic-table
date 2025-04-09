package com.periodic.backend.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;

import com.periodic.backend.domain.entity.ViewedPodcast;
import com.periodic.backend.domain.response.viewedPodcast.ViewedPodcastResponse;

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
} 