package com.periodic.backend.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.periodic.backend.domain.request.comment.element.CreateCommentElementRequest;
import com.periodic.backend.domain.request.comment.element.UpdateCommentElementRequest;
import com.periodic.backend.domain.response.comment.element.CreateCommentElementResponse;
import com.periodic.backend.domain.response.comment.element.GetCommentElementResponse;
import com.periodic.backend.domain.response.comment.element.LikeCommentElementResponse;
import com.periodic.backend.domain.response.comment.element.ToggleActiveCommentElementResponse;
import com.periodic.backend.domain.response.comment.element.UpdateCommentElementResponse;
import com.periodic.backend.domain.response.pagination.PaginationResponse;
import com.periodic.backend.service.CommentElementService;
import com.periodic.backend.util.PaginationUtils;
import com.periodic.backend.util.constant.PaginationParam;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/comments/elements")
@RequiredArgsConstructor
@Tag(name = "Element Comment API", description = "API operations for element comments")
public class CommentElementController {
    private final Logger log = LoggerFactory.getLogger(CommentElementController.class);
    private final CommentElementService commentElementService;
    
    @Operation(summary = "Create new element comment (authenticated)")
    @PostMapping("")
    public ResponseEntity<CreateCommentElementResponse> createComment(
            @RequestBody @Valid CreateCommentElementRequest request) {
    	var authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        log.info("User {} creating new comment for element ID {}", username, request.getElementId());
        return ResponseEntity.ok(commentElementService.createComment(request, username));
    }
    
    @Operation(summary = "Get element comments pageable")
    @GetMapping("")
    public ResponseEntity<PaginationResponse<List<GetCommentElementResponse>>> getComments(
            @RequestParam(defaultValue = PaginationParam.DEFAULT_CURRENT_PAGE) int current,
            @RequestParam(defaultValue = PaginationParam.DEFAULT_PAGE_SIZE) int pageSize,
            @RequestParam(required = false, defaultValue = "") String term,
            @RequestParam(required = false) Long elementId,
            @RequestParam(required = false) Long userId) {
        log.info("Getting element comments pageable");
        Pageable pageable = PaginationUtils.createPageable(current, pageSize);
        return ResponseEntity.ok(commentElementService.getComments(pageable, term, elementId, userId));
    }
    
    @Operation(summary = "Get one element comment by id")
    @GetMapping("/{id}")
    public ResponseEntity<GetCommentElementResponse> getComment(@PathVariable Long id) {
        log.info("Getting element comment with id {}", id);
        return ResponseEntity.ok(commentElementService.getComment(id));
    }
    
    @Operation(summary = "Update an element comment (owner only)")
    @PutMapping("/{id}")
    public ResponseEntity<UpdateCommentElementResponse> updateComment(
            @PathVariable Long id, 
            @RequestBody @Valid UpdateCommentElementRequest request) {
    	var authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        log.info("User {} updating element comment with id {}", username, id);
        return ResponseEntity.ok(commentElementService.updateComment(id, request, username));
    }
    
    @Operation(summary = "Toggle active for an element comment (admin)")
    @PatchMapping("/{id}/toggle-active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ToggleActiveCommentElementResponse> toggleActive(@PathVariable Long id) {
        log.info("Admin toggling active for element comment id {}", id);
        return ResponseEntity.ok(commentElementService.toggleActive(id));
    }
    
    @Operation(summary = "Like an element comment")
    @PatchMapping("/{id}/like")
    public ResponseEntity<LikeCommentElementResponse> likeComment(@PathVariable Long id) {
        log.info("Liking element comment id {}", id);
        return ResponseEntity.ok(commentElementService.likeComment(id));
    }
} 