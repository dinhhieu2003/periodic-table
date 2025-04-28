package com.periodic.backend.service;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.periodic.backend.domain.dto.notification.NotificationPayload;
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
import com.periodic.backend.security.SecurityUtils;
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
    private final NotificationService notificationService;
    private final NotificationPublisher notificationPublisher;

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

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
        } else if (term != null && !term.isEmpty()) {
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
        CommentPodcast updatedComment = new CommentPodcast();
        log.info("Podcast comment {} like count incremented.", id);

        // --- Notification Logic ---
        try {
            User author = comment.getUser();
            if (author == null) {
                log.warn("CommentPodcast {} has null author, cannot send like notification.", comment.getId());
                return commentPodcastMapper.commentPodcastToLikeCommentPodcastResponse(comment);
            }
            Long authorUserId = author.getId();

            // Get liker details
            String likerUsername = SecurityUtils.getCurrentUserLogin()
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));
            User liker = userService.getUserByEmail(likerUsername);
            Long likerUserId = liker.getId();

            // Check for self-like
            if (Objects.equals(likerUserId, authorUserId)) {
                log.info("User {} liked their own podcast comment {}, no notification sent.", likerUserId, comment.getId());
                throw new AppException(ErrorCode.CAN_NOT_SELF_LIKE);
            } else {
            	updatedComment = commentPodcastRepository.save(comment);
                String podcastTitle = updatedComment.getPodcast() != null ? updatedComment.getPodcast().getTitle() : "Unknown Podcast";
                String message = String.format("%s liked your comment on podcast '%s'.", liker.getName(), podcastTitle);
                String notificationType = "COMMENT_LIKE_PODCAST";

                // 1. Save notification to DB
                // related id is this podcast id
                Long podcastId = updatedComment.getPodcast().getId();
                notificationService.createAndSaveNotification(authorUserId, notificationType, message, podcastId);

                // 2. Prepare payload for WebSocket
                NotificationPayload payload = NotificationPayload.builder()
                    .type(notificationType)
                    .message(message)
                    .relatedId(podcastId)
                    .timestamp(Instant.now().atOffset(ZoneOffset.UTC).format(ISO_FORMATTER))
                    .build();

                // 3. Publish via WebSocket
                notificationPublisher.publishNotificationToUser(author.getEmail(), payload);
                log.info("Sent COMMENT_LIKE notification to author {} for podcast comment {} liked by user {}", authorUserId, updatedComment.getId(), likerUserId);
            }
        } catch (Exception e) {
            log.error("Error occurred during notification process for podcast comment like (commentId: {}): {}", updatedComment.getId(), e.getMessage(), e);
            // Do not re-throw, the like itself was successful
        }
        // --- End Notification Logic ---

        LikeCommentPodcastResponse response = commentPodcastMapper.commentPodcastToLikeCommentPodcastResponse(updatedComment);
        log.info("End: Function like podcast comment id {} success", id);
        return response;
    }
    
    private CommentPodcast findCommentById(Long id) {
        return commentPodcastRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));
    }
}