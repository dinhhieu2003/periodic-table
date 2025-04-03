package com.periodic.backend.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.periodic.backend.domain.entity.CommentPodcast;
import com.periodic.backend.domain.entity.Podcast;
import com.periodic.backend.domain.entity.User;
import com.periodic.backend.domain.request.comment.podcast.CreateCommentPodcastRequest;
import com.periodic.backend.domain.request.comment.podcast.UpdateCommentPodcastRequest;
import com.periodic.backend.domain.response.comment.podcast.CreateCommentPodcastResponse;
import com.periodic.backend.domain.response.comment.podcast.GetCommentPodcastResponse;
import com.periodic.backend.domain.response.comment.podcast.LikeCommentPodcastResponse;
import com.periodic.backend.domain.response.comment.podcast.ToggleActiveCommentPodcastResponse;
import com.periodic.backend.domain.response.comment.podcast.UpdateCommentPodcastResponse;
import com.periodic.backend.domain.response.pagination.PaginationResponse;
import com.periodic.backend.exception.AppException;
import com.periodic.backend.mapper.CommentPodcastMapper;
import com.periodic.backend.repository.CommentPodcastRepository;
import com.periodic.backend.repository.PodcastRepository;
import com.periodic.backend.util.PaginationUtils;
import com.periodic.backend.util.constant.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentPodcastService {
    private final Logger log = LoggerFactory.getLogger(CommentPodcastService.class);
    private final CommentPodcastRepository commentPodcastRepository;
    private final PodcastRepository podcastRepository;
    private final CommentPodcastMapper commentPodcastMapper;
    private final UserService userService;
    
    public CreateCommentPodcastResponse createComment(CreateCommentPodcastRequest request, String username) {
        log.info("Start: Create a new podcast comment");
        
        // Get podcast
        Podcast podcast = podcastRepository.findById(request.getPodcastId())
                .orElseThrow(() -> new AppException(ErrorCode.PODCAST_NOT_FOUND));
        
        // Create comment
        User currentUser = userService.getUserByEmail(username);
        CommentPodcast comment = commentPodcastMapper.createCommentPodcastRequestToCommentPodcast(request);
        comment.setPodcast(podcast);
        comment.setUser(currentUser);
        
        CommentPodcast savedComment = commentPodcastRepository.save(comment);
        CreateCommentPodcastResponse response = commentPodcastMapper.commentPodcastToCreateCommentPodcastResponse(savedComment);
        
        log.info("End: Create a new podcast comment success with ID {}", response.getId());
        return response;
    }
    
    public PaginationResponse<List<GetCommentPodcastResponse>> getComments(Pageable pageable, String term, Long podcastId, Long userId) {
        log.info("Start: Function get podcast comments pageable");
        Page<CommentPodcast> pageComment = null;
        
        if (podcastId != null) {
            log.info("Find all comments for podcast ID {}", podcastId);
            pageComment = commentPodcastRepository.findByPodcastId(pageable, podcastId);
        } else if (userId != null) {
            log.info("Find all comments by user ID {}", userId);
            pageComment = commentPodcastRepository.findByUserId(pageable, userId);
        } else if (!term.isEmpty()) {
            log.info("Find all comments containing text: {}", term);
            pageComment = commentPodcastRepository.findByContentContainingIgnoreCase(pageable, term);
        } else {
            log.info("Find all comments in database");
            pageComment = commentPodcastRepository.findAll(pageable);
        }
        
        Page<GetCommentPodcastResponse> pageData = commentPodcastMapper.pageCommentPodcastToPageGetCommentPodcastResponse(pageComment);
        PaginationResponse<List<GetCommentPodcastResponse>> response =
                PaginationUtils.buildPaginationResponse(pageable, pageData);
        
        log.info("End: Function get podcast comments pageable success");
        return response;
    }
    
    public GetCommentPodcastResponse getComment(Long id) {
        log.info("Start: Get podcast comment by id {}", id);
        CommentPodcast comment = findCommentById(id);
        GetCommentPodcastResponse response = commentPodcastMapper.commentPodcastToGetCommentPodcastResponse(comment);
        log.info("End: Get podcast comment by id {} success", id);
        return response;
    }
    
    public UpdateCommentPodcastResponse updateComment(Long id, UpdateCommentPodcastRequest request, String username) {
        log.info("Start: Function update podcast comment id {}", id);
        
        // Find existing comment
        CommentPodcast existingComment = findCommentById(id);
        
        // Check if the user is the owner of the comment
        User currentUser = userService.getUserByEmail(username);
        if (!existingComment.getUser().getId().equals(currentUser.getId())) {
            log.error("User {} is not authorized to update comment {}", currentUser.getId(), id);
            throw new AppException(ErrorCode.NOT_AUTHORIZED);
        }
        
        // Update comment content
        existingComment.setContent(request.getContent());
        
        CommentPodcast updatedComment = commentPodcastRepository.save(existingComment);
        UpdateCommentPodcastResponse response = commentPodcastMapper.commentPodcastToUpdateCommentPodcastResponse(updatedComment);
        
        log.info("End: Function update podcast comment id {} success", id);
        return response;
    }
    
    public ToggleActiveCommentPodcastResponse toggleActive(Long id) {
        log.info("Start: Function toggle active for podcast comment id {}", id);
        CommentPodcast comment = findCommentById(id);
        boolean active = !comment.isActive();
        comment.setActive(active);
        CommentPodcast updatedComment = commentPodcastRepository.save(comment);
        log.info("Update active podcast comment into database success");
        ToggleActiveCommentPodcastResponse response = commentPodcastMapper.commentPodcastToToggleActiveCommentPodcastResponse(updatedComment);
        log.info("End: Function toggle active for podcast comment id {} success", id);
        return response;
    }
    
    public LikeCommentPodcastResponse likeComment(Long id) {
        log.info("Start: Function like podcast comment id {}", id);
        CommentPodcast comment = findCommentById(id);
        comment.setLikes(comment.getLikes() + 1);
        CommentPodcast updatedComment = commentPodcastRepository.save(comment);
        LikeCommentPodcastResponse response = commentPodcastMapper.commentPodcastToLikeCommentPodcastResponse(updatedComment);
        log.info("End: Function like podcast comment id {} success", id);
        return response;
    }
    
    private CommentPodcast findCommentById(Long id) {
        return commentPodcastRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));
    }
}