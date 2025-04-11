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

import com.periodic.backend.domain.response.favoritePodcast.CheckActiveFavoritePodcastResponse;
import com.periodic.backend.domain.response.favoritePodcast.FavoritePodcastResponse;
import com.periodic.backend.domain.response.favoritePodcast.ToggleActiveFavoritePodcastResponse;
import com.periodic.backend.domain.response.pagination.PaginationResponse;
import com.periodic.backend.service.FavoritePodcastService;
import com.periodic.backend.util.PaginationUtils;
import com.periodic.backend.util.constant.PaginationParam;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/favorite-podcasts")
@RequiredArgsConstructor
@Slf4j
public class FavoritePodcastController {
	private final FavoritePodcastService favoritePodcastService;
	
	@GetMapping("")
	public ResponseEntity<PaginationResponse<List<FavoritePodcastResponse>>> getFavoritePodcasts(
			@RequestParam(defaultValue = PaginationParam.DEFAULT_CURRENT_PAGE) int current,
			@RequestParam(defaultValue = PaginationParam.DEFAULT_PAGE_SIZE) int pageSize,
			@RequestParam(required = false, defaultValue = "") String term,
			@RequestParam(required = false) String[] sortBy,
			@RequestParam(required = false) String[] sortDirection,
			@RequestParam(required = false) Boolean active) {
		log.info("Getting favorite podcast list of this user");
		Pageable pageable = PaginationUtils.createPageable(current, pageSize);
		return ResponseEntity.ok(favoritePodcastService.getFavoritePodcasts(pageable, term, sortBy, sortDirection, active));
	}
	
	@PostMapping("/podcasts/{id}")
	@Operation(summary = "Toggle active an favorite podcast with element id - {id} is podcastId")
	public ResponseEntity<ToggleActiveFavoritePodcastResponse> toggleActive(@PathVariable Long id) {
		log.info("Toggle love for podcast id {} of this user", id);
		return ResponseEntity.ok(favoritePodcastService.toggleActive(id));
	}
	
	@GetMapping("/podcasts/{id}")
	@Operation(summary = "Check active a favorite podcast with podcast id - {id} is podcastId")
	public ResponseEntity<CheckActiveFavoritePodcastResponse> checkActive(@PathVariable Long id) {
		log.info("Check active for podcast id {} of this user", id);
		return ResponseEntity.ok(favoritePodcastService.checkActive(id));
	}
}
