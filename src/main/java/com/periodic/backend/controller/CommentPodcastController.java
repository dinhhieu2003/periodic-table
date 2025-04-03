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

import com.periodic.backend.domain.request.comment.podcast.CreateCommentPodcastRequest;
import com.periodic.backend.domain.request.comment.podcast.UpdateCommentPodcastRequest;
import com.periodic.backend.domain.response.comment.podcast.CreateCommentPodcastResponse;
import com.periodic.backend.domain.response.comment.podcast.GetCommentPodcastResponse;
import com.periodic.backend.domain.response.comment.podcast.LikeCommentPodcastResponse;
import com.periodic.backend.domain.response.comment.podcast.ToggleActiveCommentPodcastResponse;
import com.periodic.backend.domain.response.comment.podcast.UpdateCommentPodcastResponse;
import com.periodic.backend.domain.response.pagination.PaginationResponse;
import com.periodic.backend.service.CommentPodcastService;
import com.periodic.backend.util.PaginationUtils;
import com.periodic.backend.util.constant.PaginationParam;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/comments/podcasts")
@RequiredArgsConstructor
@Tag(name = "Podcast Comment API", description = "API operations for podcast comments")
public class CommentPodcastController {
    private final Logger log = LoggerFactory.getLogger(CommentPodcastController.class);
    private final CommentPodcastService commentPodcastService;
    
    @Operation(summary = "Create new podcast comment (authenticated)")
    @PostMapping("")
    public ResponseEntity<CreateCommentPodcastResponse> createComment(
            @RequestBody @Valid CreateCommentPodcastRequest request) {
    	var authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        log.info("User {} creating new comment for podcast ID {}", username, request.getPodcastId());
        return ResponseEntity.ok(commentPodcastService.createComment(request, username));
    }
    
    @Operation(summary = "Get podcast comments pageable")
    @GetMapping("")
    public ResponseEntity<PaginationResponse<List<GetCommentPodcastResponse>>> getComments(
            @RequestParam(defaultValue = PaginationParam.DEFAULT_CURRENT_PAGE) int current,
            @RequestParam(defaultValue = PaginationParam.DEFAULT_PAGE_SIZE) int pageSize,
            @RequestParam(required = false, defaultValue = "") String term,
            @RequestParam(required = false) Long podcastId,
            @RequestParam(required = false) Long userId) {
        log.info("Getting podcast comments pageable");
        Pageable pageable = PaginationUtils.createPageable(current, pageSize);
        return ResponseEntity.ok(commentPodcastService.getComments(pageable, term, podcastId, userId));
    }
    
    @Operation(summary = "Get one podcast comment by id")
    @GetMapping("/{id}")
    public ResponseEntity<GetCommentPodcastResponse> getComment(@PathVariable Long id) {
        log.info("Getting podcast comment with id {}", id);
        return ResponseEntity.ok(commentPodcastService.getComment(id));
    }
    
    @Operation(summary = "Update a podcast comment (owner only)")
    @PutMapping("/{id}")
    public ResponseEntity<UpdateCommentPodcastResponse> updateComment(
            @PathVariable Long id, 
            @RequestBody @Valid UpdateCommentPodcastRequest request) {
    	var authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        log.info("User {} updating podcast comment with id {}", username, id);
        return ResponseEntity.ok(commentPodcastService.updateComment(id, request, username));
    }
    
    @Operation(summary = "Toggle active for a podcast comment (admin)")
    @PatchMapping("/{id}/toggle-active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ToggleActiveCommentPodcastResponse> toggleActive(@PathVariable Long id) {
        log.info("Admin toggling active for podcast comment id {}", id);
        return ResponseEntity.ok(commentPodcastService.toggleActive(id));
    }
    
    @Operation(summary = "Like a podcast comment")
    @PatchMapping("/{id}/like")
    public ResponseEntity<LikeCommentPodcastResponse> likeComment(@PathVariable Long id) {
        log.info("Liking podcast comment id {}", id);
        return ResponseEntity.ok(commentPodcastService.likeComment(id));
    }
} 