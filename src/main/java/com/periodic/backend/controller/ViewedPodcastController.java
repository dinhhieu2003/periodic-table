package com.periodic.backend.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.periodic.backend.domain.response.pagination.PaginationResponse;
import com.periodic.backend.domain.response.viewedPodcast.ViewedPodcastResponse;
import com.periodic.backend.service.ViewedPodcastService;
import com.periodic.backend.util.PaginationUtils;
import com.periodic.backend.util.constant.PaginationParam;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/viewed-podcasts")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Viewed Pocast API", description = "API operation for viewed podcasts")
public class ViewedPodcastController {
	private final ViewedPodcastService viewedPodcastService;
	
	@Operation(summary = "Create and update viewedPodcast - {id} is podcastId")
	@PostMapping("/podcasts/{id}")
	// create by element id
	public ResponseEntity<ViewedPodcastResponse> create(@PathVariable Long id) {
		log.info("Create/update viewed podcast");
		return ResponseEntity.ok(viewedPodcastService.create(id));
	}

	@GetMapping("")
	public ResponseEntity<PaginationResponse<List<ViewedPodcastResponse>>> getViewedPodcasts(
			@RequestParam(defaultValue = PaginationParam.DEFAULT_CURRENT_PAGE) int current,
			@RequestParam(defaultValue = PaginationParam.DEFAULT_PAGE_SIZE) int pageSize,
			@RequestParam(required = false, defaultValue = "") String term,
			@RequestParam(required = false) String[] sortBy,
			@RequestParam(required = false) String[] sortDirection,
			@RequestParam(required = false) Boolean active) {
		log.info("Getting viewed elements with search, sort and active filter");
		Pageable pageable = PaginationUtils.createPageable(current, pageSize);
		return ResponseEntity.ok(viewedPodcastService.getViewedPodcasts(pageable, term, sortBy, sortDirection, active));
	}
}
