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

import com.periodic.backend.domain.request.milestone.CreateMilestoneRequest;
import com.periodic.backend.domain.request.milestone.UpdateMilestoneRequest;
import com.periodic.backend.domain.response.milestone.CreateMilestoneResponse;
import com.periodic.backend.domain.response.milestone.GetMilestoneResponse;
import com.periodic.backend.domain.response.milestone.ToggleActiveMilestoneResponse;
import com.periodic.backend.domain.response.milestone.UpdateMilestoneResponse;
import com.periodic.backend.domain.response.pagination.PaginationResponse;
import com.periodic.backend.service.MilestoneService;
import com.periodic.backend.util.PaginationUtils;
import com.periodic.backend.util.constant.PaginationParam;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/milestones")
@RequiredArgsConstructor
@Tag(name = "Milestone API", description = "API operations for milestones")
public class MilestoneController {
    private final Logger log = LoggerFactory.getLogger(MilestoneController.class);
    private final MilestoneService milestoneService;
    
    @Operation(summary = "Create new milestone (admin)")
    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CreateMilestoneResponse> createMilestone(@RequestBody @Valid CreateMilestoneRequest request) {
        log.info("Admin creating new milestone for scientist ID {}", request.getScientistId());
        return ResponseEntity.ok(milestoneService.createMilestone(request));
    }
    
    @Operation(summary = "Get milestones pageable")
    @GetMapping("")
    public ResponseEntity<PaginationResponse<List<GetMilestoneResponse>>> getMilestones(
            @RequestParam(defaultValue = PaginationParam.DEFAULT_CURRENT_PAGE) int current,
            @RequestParam(defaultValue = PaginationParam.DEFAULT_PAGE_SIZE) int pageSize,
            @RequestParam(required = false, defaultValue = "") String term,
            @RequestParam(required = false, defaultValue = "") String searchBy) {
        log.info("User is getting milestones pageable");
        Pageable pageable = PaginationUtils.createPageable(current, pageSize);
        return ResponseEntity.ok(milestoneService.getMilestones(pageable, term, searchBy));
    }
    
    @Operation(summary = "Get one milestone by milestone id")
    @GetMapping("/{id}")
    public ResponseEntity<GetMilestoneResponse> getMilestone(@PathVariable Long id) {
        log.info("User is getting a milestone with id {}", id);
        return ResponseEntity.ok(milestoneService.getMilestone(id));
    }
    
    @Operation(summary = "Update a milestone by milestone id (admin)")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UpdateMilestoneResponse> updateMilestone(
            @PathVariable Long id, @RequestBody @Valid UpdateMilestoneRequest request) {
        log.info("Admin is updating milestone with id {}", id);
        return ResponseEntity.ok(milestoneService.updateMilestone(id, request));
    }
    
    @Operation(summary = "Toggle active for a milestone by milestone id (admin)")
    @PatchMapping("/{id}/toggle-active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ToggleActiveMilestoneResponse> toggleActive(@PathVariable Long id) {
        log.info("Admin is toggling active for milestone id {}", id);
        return ResponseEntity.ok(milestoneService.toggleActive(id));
    }
} 