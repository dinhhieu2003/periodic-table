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

import com.periodic.backend.domain.response.favoriteElement.FavoriteElementResponse;
import com.periodic.backend.domain.response.pagination.PaginationResponse;
import com.periodic.backend.service.FavoriteElementService;
import com.periodic.backend.util.PaginationUtils;
import com.periodic.backend.util.constant.PaginationParam;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/favorite-elements")
@RequiredArgsConstructor
@Slf4j
public class FavoriteElementController {
	private final FavoriteElementService favoriteElementService;
	
	@GetMapping("")
	public ResponseEntity<PaginationResponse<List<FavoriteElementResponse>>> getFavoriteElements(
			@RequestParam(defaultValue = PaginationParam.DEFAULT_CURRENT_PAGE) int current,
			@RequestParam(defaultValue = PaginationParam.DEFAULT_PAGE_SIZE) int pageSize,
			@RequestParam(required = false, defaultValue = "") String term,
			@RequestParam(required = false) String[] sortBy,
			@RequestParam(required = false) String[] sortDirection,
			@RequestParam(required = false) Boolean active) {
		log.info("Getting favorite element list of this user");
		Pageable pageable = PaginationUtils.createPageable(current, pageSize);
		return ResponseEntity.ok(favoriteElementService.getFavoriteElements(pageable, term, sortBy, sortDirection, active));
	}
	
	@PostMapping("/elements/{id}")
	@Operation(summary = "Toggle active an favorite element with element id - {id} is elementId")
	public ResponseEntity<FavoriteElementResponse> toggleActive(@PathVariable Long id) {
		log.info("Toggle love for element id {} of this user", id);
		return ResponseEntity.ok(favoriteElementService.toggleActive(id));
	}
}
