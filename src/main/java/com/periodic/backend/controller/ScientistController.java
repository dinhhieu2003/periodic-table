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

import com.periodic.backend.domain.request.scientist.CreateScientistRequest;
import com.periodic.backend.domain.request.scientist.UpdateScientistRequest;
import com.periodic.backend.domain.response.pagination.PaginationResponse;
import com.periodic.backend.domain.response.scientist.CreateScientistResponse;
import com.periodic.backend.domain.response.scientist.GetScientistResponse;
import com.periodic.backend.domain.response.scientist.ToggleActiveScientistResponse;
import com.periodic.backend.domain.response.scientist.UpdateScientistResponse;
import com.periodic.backend.service.ScientistService;
import com.periodic.backend.util.PaginationUtils;
import com.periodic.backend.util.constant.PaginationParam;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/scientists")
@RequiredArgsConstructor
@Tag(name = "Scientist API", description = "API operation for scientist")
public class ScientistController {
	private final Logger log = LoggerFactory.getLogger(ScientistController.class);
	private final ScientistService scientistService;
	
	@Operation(summary = "Create new scientist (admin)")
	@PostMapping("")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<CreateScientistResponse> createScientist(@RequestBody @Valid CreateScientistRequest createScientistRequest) {
		log.info("Admin creating new scientist");
		return ResponseEntity.ok(scientistService.createScientist(createScientistRequest));
	}
	
	// get all
	@Operation(summary = "Get scientists with search, sort and active filter")
	@GetMapping("")
	public ResponseEntity<PaginationResponse<List<GetScientistResponse>>> getScientists(
			@RequestParam(defaultValue = PaginationParam.DEFAULT_CURRENT_PAGE) int current,
			@RequestParam(defaultValue = PaginationParam.DEFAULT_PAGE_SIZE) int pageSize,
			@RequestParam(required = false, defaultValue = "") String term,
			@RequestParam(required = false) String[] sortBy,
			@RequestParam(required = false) String[] sortDirection,
			@RequestParam(required = false) Boolean active) {
		log.info("Getting scientists with search, sort and active filter");
		Pageable pageable = PaginationUtils.createPageable(current, pageSize);
		return ResponseEntity.ok(scientistService.getScientists(pageable, term, sortBy, sortDirection, active));
	}
	
	// get one
	@GetMapping("/{id}")
	public ResponseEntity<GetScientistResponse> getScientist(@PathVariable Long id) {
		log.info("User is getting a scientist with id {}", id);
		return ResponseEntity.ok(scientistService.getScientist(id));
	}
	
	// update
	@Operation(summary = "Update a scientist by scientist id (admin)")
	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<UpdateScientistResponse> updateScientist(@PathVariable Long id, 
			@RequestBody @Valid UpdateScientistRequest request) {
		log.info("Admin is updating scientist with id {}", id);
		return ResponseEntity.ok(scientistService.updateScientist(id, request));
	}
	
	// toggle active
	@Operation(summary = "Toggle active for a scientist by scientist id (admin)")
	@PatchMapping("/{id}/toggle-active")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ToggleActiveScientistResponse> toggleActive(@PathVariable Long id) {
		log.info("Admin is toggling active for scientist id {}", id);
		return ResponseEntity.ok(scientistService.toggleActive(id));
	}
}
