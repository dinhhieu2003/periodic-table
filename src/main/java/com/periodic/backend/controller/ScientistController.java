package com.periodic.backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.periodic.backend.domain.request.scientist.CreateScientistRequest;
import com.periodic.backend.domain.response.scientist.CreateScientistResponse;
import com.periodic.backend.service.ScientistService;

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
	
	@PostMapping("")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<CreateScientistResponse> createScientist(@RequestBody @Valid CreateScientistRequest createScientistRequest) {
		log.info("Admin creating new scientist");
		return ResponseEntity.ok(scientistService.createScientist(createScientistRequest));
	}
}
