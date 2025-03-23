package com.periodic.backend.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.periodic.backend.domain.request.element.CreateElementRequest;
import com.periodic.backend.domain.request.element.UpdateElementRequest;
import com.periodic.backend.domain.response.element.CreateElementResponse;
import com.periodic.backend.domain.response.element.GetElementResponse;
import com.periodic.backend.domain.response.element.UpdateElementResponse;
import com.periodic.backend.domain.response.pagination.PaginationResponse;
import com.periodic.backend.service.ElementService;
import com.periodic.backend.util.PaginationUtils;
import com.periodic.backend.util.constant.PaginationParam;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/elements")
@RequiredArgsConstructor
@Tag(name = "Element API", description = "API operation for elements")
public class ElementController {
	private final ElementService elementService;
	private final Logger log = LoggerFactory.getLogger(ElementController.class);
	
	@PostMapping("")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<CreateElementResponse> createElement(@RequestBody @Valid CreateElementRequest createElementRequest) {
		log.info("Admin is creating a new element");
		return ResponseEntity.ok(elementService.createElement(createElementRequest));
	}
	
	@GetMapping("")
	public ResponseEntity<PaginationResponse<List<GetElementResponse>>> getElements(
			@RequestParam(defaultValue = PaginationParam.DEFAULT_CURRENT_PAGE) int current,
			@RequestParam(defaultValue = PaginationParam.DEFAULT_PAGE_SIZE) int pageSize,
			@RequestParam(required = false, defaultValue = "") String term) {
		log.info("User getting elements");
		Pageable pageable = PaginationUtils.createPageable(current, pageSize);
		return ResponseEntity.ok(elementService.getElements(pageable, term));
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<GetElementResponse> getElement(@PathVariable long id) {
		log.info("User getting element id {}", id);
		return ResponseEntity.ok(elementService.getElement(id));
	}
	
	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<UpdateElementResponse> updateElement(@RequestBody @Valid UpdateElementRequest updateElementRequest, @PathVariable long id) {
		log.info("Admin updating element id {}", id);
		return ResponseEntity.ok(elementService.updateElement(updateElementRequest, id));
	}
}
