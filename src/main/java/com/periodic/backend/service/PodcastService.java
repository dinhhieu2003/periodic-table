package com.periodic.backend.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.periodic.backend.domain.entity.Element;
import com.periodic.backend.domain.entity.Podcast;
import com.periodic.backend.domain.request.podcast.CreatePodcastRequest;
import com.periodic.backend.domain.request.podcast.UpdatePodcastRequest;
import com.periodic.backend.domain.response.pagination.PaginationResponse;
import com.periodic.backend.domain.response.podcast.CreatePodcastResponse;
import com.periodic.backend.domain.response.podcast.GetPodcastResponse;
import com.periodic.backend.domain.response.podcast.ToggleActivePodcastResponse;
import com.periodic.backend.domain.response.podcast.UpdatePodcastResponse;
import com.periodic.backend.exception.AppException;
import com.periodic.backend.mapper.PodcastMapper;
import com.periodic.backend.repository.PodcastRepository;
import com.periodic.backend.util.PaginationUtils;
import com.periodic.backend.util.constant.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PodcastService {
	private final Logger log = LoggerFactory.getLogger(PodcastService.class);
	private final PodcastRepository podcastRepository;
	private final ElementService elementService;
	private final PodcastMapper podcastMapper;
	
	public CreatePodcastResponse createPodcast(CreatePodcastRequest request) {
		log.info("Start: Function create podcast");
		Podcast podcast = podcastMapper.createPodcastRequestToPodcast(request);
		Element element = elementService.getElementById(request.getElementId());
		podcast.setElement(element);
		Podcast newPodcast = podcastRepository.save(podcast);
		log.info("Save new podcast into database success");
		CreatePodcastResponse response = podcastMapper.podcastToCreatePodcastResponse(newPodcast);
		log.info("End: Function create podcast success");
		return response;
	}
	
	public PaginationResponse<List<GetPodcastResponse>> getPodcasts(Pageable pageable, String term) {
		log.info("Start: Function get all podcasts pageable");
		Page<Podcast> pagePodcast = null;
		Page<GetPodcastResponse> pageData = null;
		if(term.isEmpty()) {
			log.info("Find all podcasts in database");
			pagePodcast = podcastRepository.findAll(pageable);
		} else {
			log.info("Find all podcasts in database with title {}", term);
			pagePodcast = podcastRepository.findByTitleContainingIgnoreCase(pageable, term);
		}
		pageData = podcastMapper.pagePodcastToPageGetPodcastResponse(pagePodcast);
		PaginationResponse<List<GetPodcastResponse>> response = 
				PaginationUtils.buildPaginationResponse(pageable, pageData);
		return response;
	}
	
	public GetPodcastResponse getPodcast(Long id) {
		log.info("Start: Function get podcast id {}", id);
		Podcast podcast = podcastRepository.findById(id)
				.orElseThrow(() -> new AppException(ErrorCode.PODCAST_NOT_FOUND));
		GetPodcastResponse response = podcastMapper.podcastToGetPodcastResponse(podcast);
		log.info("End: Function get podcast id {} success", id);
		return response;
	}
	
	public UpdatePodcastResponse updatePodcast(Long id, UpdatePodcastRequest request) {
		log.info("Start: Function update podcast id {}", id);
		Podcast existPodcast = podcastRepository.findById(id)
				.orElseThrow(() -> new AppException(ErrorCode.PODCAST_NOT_FOUND));
		Podcast requestPodcast = podcastMapper.updatePodcastRequestToPodcast(request);
		requestPodcast.setId(existPodcast.getId());
		Podcast updatedPodcast = podcastRepository.save(requestPodcast);
		log.info("Update new podcast into database success");
		UpdatePodcastResponse response = podcastMapper.podcastToUpdatePodcastResponse(updatedPodcast);
		log.info("End: Function update podcast id {}", response.getId());
		return response;
	}
	
	public ToggleActivePodcastResponse toggleActive(Long id) {
		log.info("Start: Function toggle active for podcast id {}", id);
		Podcast podcast = podcastRepository.findById(id)
				.orElseThrow(() -> new AppException(ErrorCode.PODCAST_NOT_FOUND));
		boolean active = !podcast.isActive();
		podcast.setActive(active);
		Podcast updatedPodcast = podcastRepository.save(podcast);
		log.info("Update active for podcast id {} into database success", id);
		ToggleActivePodcastResponse response = podcastMapper.podcastToToggleActivePodcastResponse(updatedPodcast);
		log.info("End: Function toggle active for podcast id {} success", id);
		return response;
	}
}
