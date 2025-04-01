package com.periodic.backend.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;

import com.periodic.backend.domain.entity.Podcast;
import com.periodic.backend.domain.request.podcast.CreatePodcastRequest;
import com.periodic.backend.domain.request.podcast.UpdatePodcastRequest;
import com.periodic.backend.domain.response.podcast.CreatePodcastResponse;
import com.periodic.backend.domain.response.podcast.GetPodcastResponse;
import com.periodic.backend.domain.response.podcast.ToggleActivePodcastResponse;
import com.periodic.backend.domain.response.podcast.UpdatePodcastResponse;

@Component
@Mapper(componentModel = "spring")
public interface PodcastMapper {
	PodcastMapper INSTANCE = Mappers.getMapper(PodcastMapper.class);
	
	@Mapping(target = "element", ignore = true)
	Podcast createPodcastRequestToPodcast(CreatePodcastRequest createPodcastRequest);
	
	@Mapping(target = "element", ignore = true)
	Podcast updatePodcastRequestToPodcast(UpdatePodcastRequest updatePodcastRequest);
	
	@Mapping(source = "element.name", target = "element")
	CreatePodcastResponse podcastToCreatePodcastResponse(Podcast podcast);
	
	@Mapping(source = "element.name", target = "element")
	UpdatePodcastResponse podcastToUpdatePodcastResponse(Podcast podcast);
	
	@Mapping(source = "element.name", target = "element")
	GetPodcastResponse podcastToGetPodcastResponse(Podcast podcast);
	
	default Page<GetPodcastResponse> pagePodcastToPageGetPodcastResponse(Page<Podcast> pagePodcast) {
		List<GetPodcastResponse> content = pagePodcast.getContent()
				.stream()
				.map(this::podcastToGetPodcastResponse)
				.collect(Collectors.toList());
		return new PageImpl<>(content, pagePodcast.getPageable(), pagePodcast.getTotalElements());
	}
	
	default ToggleActivePodcastResponse podcastToToggleActivePodcastResponse(Podcast podcast) {
		return new ToggleActivePodcastResponse(podcast.getId(), podcast.isActive());
	}
}
