package com.periodic.backend.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.periodic.backend.domain.request.podcast.CreatePodcastRequest;
import com.periodic.backend.domain.request.podcast.UpdatePodcastRequest;
import com.periodic.backend.domain.response.pagination.PaginationResponse;
import com.periodic.backend.domain.response.podcast.CreatePodcastResponse;
import com.periodic.backend.domain.response.podcast.GetPodcastResponse;
import com.periodic.backend.domain.response.podcast.ToggleActivePodcastResponse;
import com.periodic.backend.domain.response.podcast.UpdatePodcastResponse;
import com.periodic.backend.service.PodcastService;
import com.periodic.backend.util.PaginationUtils;
import com.periodic.backend.util.constant.PaginationParam;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/podcasts")
@RequiredArgsConstructor
@Tag(name = "Podcast API", description = "API operation for podcasts")
public class PodcastController {
	private final Logger log = LoggerFactory.getLogger(PodcastController.class);
	private final PodcastService podcastService;
	
	// create 
	@Operation(summary = "Create new podcast (admin)")
	@PostMapping("")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<CreatePodcastResponse> createPodcast(@Valid @RequestBody CreatePodcastRequest request) {
		log.info("Admin is creating a podcast for element id {}", request.getElementId());
		return ResponseEntity.ok(podcastService.createPodcast(request));
	}
	
	// get all
	@Operation(summary = "Get podcasts with search, sort and active filter")
	@GetMapping("")
	public ResponseEntity<PaginationResponse<List<GetPodcastResponse>>> getPodcasts(
			@RequestParam(defaultValue = PaginationParam.DEFAULT_CURRENT_PAGE) int current,
			@RequestParam(defaultValue = PaginationParam.DEFAULT_PAGE_SIZE) int pageSize,
			@RequestParam(required = false, defaultValue = "") String term,
			@RequestParam(required = false) String[] sortBy,
			@RequestParam(required = false) String[] sortDirection,
			@RequestParam(required = false) Boolean active) {
		log.info("Getting podcasts with search, sort and active filter");
		Pageable pageable = PaginationUtils.createPageable(current, pageSize);
		return ResponseEntity.ok(podcastService.getPodcasts(pageable, term, sortBy, sortDirection, active));
	}
	
	// get one
	@Operation(summary = "Get one podcast by id")
	@GetMapping("/{id}")
	public ResponseEntity<GetPodcastResponse> getPodcast(@PathVariable Long id) {
		log.info("User is getting a podcast id {}", id);
		return ResponseEntity.ok(podcastService.getPodcast(id));
	}
	
	// update
	@Operation(summary = "Update a podcast by podcast id (admin)")
	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<UpdatePodcastResponse> updatePodcast(
			@Valid @RequestBody UpdatePodcastRequest request,
			@PathVariable Long id) {
		log.info("Admin is updating podcast with id {}", id);
		return ResponseEntity.ok(podcastService.updatePodcast(id, request));
	}
	
	// toggle active
	@Operation(summary = "Toggle active for a podcast by podcast id (admin)")
	@PatchMapping("/{id}/toggle-active")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ToggleActivePodcastResponse> toggleActive(@PathVariable Long id) {
		log.info("Admin is toggling active for podcast id {}", id);
		return ResponseEntity.ok(podcastService.toggleActive(id));
	}

	@Operation(summary = "Get podcasts by element ID pageable")
	@GetMapping("/by-element/{elementId}")
	public ResponseEntity<PaginationResponse<List<GetPodcastResponse>>> getPodcastsByElementId(
	        @RequestParam(defaultValue = PaginationParam.DEFAULT_CURRENT_PAGE) int current,
	        @RequestParam(defaultValue = PaginationParam.DEFAULT_PAGE_SIZE) int pageSize,
	        @PathVariable Long elementId) {
	    log.info("Getting podcasts for element ID {} pageable", elementId);
	    Pageable pageable = PaginationUtils.createPageable(current, pageSize);
	    return ResponseEntity.ok(podcastService.getPodcastsByElementId(pageable, elementId));
	}
	
}
