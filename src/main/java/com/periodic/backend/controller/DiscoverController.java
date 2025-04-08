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

import com.periodic.backend.domain.request.discover.CreateDiscoverRequest;
import com.periodic.backend.domain.request.discover.UpdateDiscoverRequest;
import com.periodic.backend.domain.response.discover.CreateDiscoverResponse;
import com.periodic.backend.domain.response.discover.GetDiscoverResponse;
import com.periodic.backend.domain.response.discover.ToggleActiveDiscoverResponse;
import com.periodic.backend.domain.response.discover.UpdateDiscoverResponse;
import com.periodic.backend.domain.response.pagination.PaginationResponse;
import com.periodic.backend.service.DiscoverService;
import com.periodic.backend.util.PaginationUtils;
import com.periodic.backend.util.constant.PaginationParam;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/discoveries")
@RequiredArgsConstructor
@Tag(name = "Discovery API", description = "API operations for discoveries")
public class DiscoverController {
    private final Logger log = LoggerFactory.getLogger(DiscoverController.class);
    private final DiscoverService discoverService;
    
    @Operation(summary = "Create new discovery (admin)")
    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CreateDiscoverResponse> createDiscover(@RequestBody @Valid CreateDiscoverRequest request) {
        log.info("Admin creating new discovery for scientist ID {} and element ID {}", 
                request.getScientistId(), request.getElementId());
        return ResponseEntity.ok(discoverService.createDiscover(request));
    }
    
    @Operation(summary = "Get discoveries with search, sort and active filter")
	@GetMapping("")
	public ResponseEntity<PaginationResponse<List<GetDiscoverResponse>>> getDiscoveries(
			@RequestParam(defaultValue = PaginationParam.DEFAULT_CURRENT_PAGE) int current,
			@RequestParam(defaultValue = PaginationParam.DEFAULT_PAGE_SIZE) int pageSize,
			@RequestParam(required = false, defaultValue = "") String term,
			@RequestParam(required = false) String[] sortBy,
			@RequestParam(required = false) String[] sortDirection,
			@RequestParam(required = false) Boolean active) {
		log.info("Getting discoveries with search, sort and active filter");
		Pageable pageable = PaginationUtils.createPageable(current, pageSize);
		return ResponseEntity.ok(discoverService.getDiscoveries(pageable, term, sortBy, sortDirection, active));
	}
    
    @Operation(summary = "Get one discovery by discovery id")
    @GetMapping("/{id}")
    public ResponseEntity<GetDiscoverResponse> getDiscover(@PathVariable Long id) {
        log.info("User is getting a discovery with id {}", id);
        return ResponseEntity.ok(discoverService.getDiscover(id));
    }
    
    @Operation(summary = "Update a discovery by discovery id (admin)")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UpdateDiscoverResponse> updateDiscover(
            @PathVariable Long id, @RequestBody @Valid UpdateDiscoverRequest request) {
        log.info("Admin is updating discovery with id {}", id);
        return ResponseEntity.ok(discoverService.updateDiscover(id, request));
    }
    
    @Operation(summary = "Toggle active for a discovery by discovery id (admin)")
    @PatchMapping("/{id}/toggle-active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ToggleActiveDiscoverResponse> toggleActive(@PathVariable Long id) {
        log.info("Admin is toggling active for discovery id {}", id);
        return ResponseEntity.ok(discoverService.toggleActive(id));
    }
} 