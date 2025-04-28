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
import com.periodic.backend.domain.entity.CommentElement;
import com.periodic.backend.domain.entity.Element;
import com.periodic.backend.domain.entity.User;
import com.periodic.backend.domain.request.comment.element.CreateCommentElementRequest;
import com.periodic.backend.domain.request.comment.element.UpdateCommentElementRequest;
import com.periodic.backend.domain.response.comment.element.CreateCommentElementResponse;
import com.periodic.backend.domain.response.comment.element.GetCommentElementResponse;
import com.periodic.backend.domain.response.comment.element.LikeCommentElementResponse;
import com.periodic.backend.domain.response.comment.element.ToggleActiveCommentElementResponse;
import com.periodic.backend.domain.response.comment.element.UpdateCommentElementResponse;
import com.periodic.backend.domain.response.pagination.PaginationResponse;
import com.periodic.backend.exception.AppException;
import com.periodic.backend.mapper.CommentElementMapper;
import com.periodic.backend.repository.CommentElementRepository;
import com.periodic.backend.security.SecurityUtils;
import com.periodic.backend.util.PaginationUtils;
import com.periodic.backend.util.constant.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentElementService {
    private final Logger log = LoggerFactory.getLogger(CommentElementService.class);
    private final CommentElementRepository commentElementRepository;
    private final ElementService elementService;
    private final UserService userService;
    private final CommentElementMapper commentElementMapper;
    private final NotificationService notificationService;
    private final NotificationPublisher notificationPublisher;

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    
    public CreateCommentElementResponse createComment(CreateCommentElementRequest request, String username) {
        log.info("Start: Create a new element comment");
        
        // Get element
        Element element = elementService.getElementById(request.getElementId());
        
        // Create comment
        User currentUser = userService.getUserByEmail(username);
        CommentElement comment = commentElementMapper.createCommentElementRequestToCommentElement(request);
        comment.setElement(element);
        comment.setUser(currentUser);
        
        CommentElement savedComment = commentElementRepository.save(comment);
        CreateCommentElementResponse response = commentElementMapper.commentElementToCreateCommentElementResponse(savedComment);
        
        log.info("End: Create a new element comment success with ID {}", response.getId());
        return response;
    }
    
    public PaginationResponse<List<GetCommentElementResponse>> getComments(Pageable pageable, String term, Long elementId, Long userId) {
        log.info("Start: Function get element comments pageable");
        Page<CommentElement> pageComment = null;
        
        if (elementId != null) {
            log.info("Find all comments for element ID {}", elementId);
            pageComment = commentElementRepository.findByElementId(pageable, elementId);
        } else if (userId != null) {
            log.info("Find all comments by user ID {}", userId);
            pageComment = commentElementRepository.findByUserId(pageable, userId);
        } else if (term != null && !term.isEmpty()) { // Check for null before isEmpty
            log.info("Find all comments containing text: {}", term);
            pageComment = commentElementRepository.findByContentContainingIgnoreCase(pageable, term);
        } else {
            log.info("Find all comments in database");
            pageComment = commentElementRepository.findAll(pageable);
        }
        
        Page<GetCommentElementResponse> pageData = commentElementMapper.pageCommentElementToPageGetCommentElementResponse(pageComment);
        PaginationResponse<List<GetCommentElementResponse>> response =
                PaginationUtils.buildPaginationResponse(pageable, pageData);
        
        log.info("End: Function get element comments pageable success");
        return response;
    }
    
    public GetCommentElementResponse getComment(Long id) {
        log.info("Start: Get element comment by id {}", id);
        CommentElement comment = findCommentById(id);
        GetCommentElementResponse response = commentElementMapper.commentElementToGetCommentElementResponse(comment);
        log.info("End: Get element comment by id {} success", id);
        return response;
    }
    
    public UpdateCommentElementResponse updateComment(Long id, UpdateCommentElementRequest request, String username) {
        log.info("Start: Function update element comment id {}", id);
        
        // Find existing comment
        CommentElement existingComment = findCommentById(id);
        
        // Check if the user is the owner of the comment
        User currentUser = userService.getUserByEmail(username);
        if (!existingComment.getUser().getId().equals(currentUser.getId())) {
            log.error("User {} is not authorized to update comment {}", currentUser.getId(), id);
            throw new AppException(ErrorCode.NOT_AUTHORIZED);
        }
        
        // Update comment content
        existingComment.setContent(request.getContent());
        
        CommentElement updatedComment = commentElementRepository.save(existingComment);
        UpdateCommentElementResponse response = commentElementMapper.commentElementToUpdateCommentElementResponse(updatedComment);
        
        log.info("End: Function update element comment id {} success", id);
        return response;
    }
    
    public ToggleActiveCommentElementResponse toggleActive(Long id) {
        log.info("Start: Function toggle active for element comment id {}", id);
        CommentElement comment = findCommentById(id);
        boolean active = !comment.isActive();
        comment.setActive(active);
        CommentElement updatedComment = commentElementRepository.save(comment);
        log.info("Update active element comment into database success");
        ToggleActiveCommentElementResponse response = commentElementMapper.commentElementToToggleActiveCommentElementResponse(updatedComment);
        log.info("End: Function toggle active for element comment id {} success", id);
        return response;
    }
    
    public LikeCommentElementResponse likeComment(Long id) {
        log.info("Start: Function like element comment id {}", id);
        CommentElement comment = findCommentById(id);
        comment.setLikes(comment.getLikes() + 1);
        log.info("Element comment {} like count incremented.", id);
        CommentElement updatedComment = new CommentElement();
        // --- Notification Logic --- 
        try {
            User author = comment.getUser();
            if (author == null) {
                log.warn("CommentElement {} has null author, cannot send like notification.", updatedComment.getId());
                return commentElementMapper.commentElementToLikeCommentElementResponse(updatedComment);
            }
            Long authorUserId = author.getId();

            // Get liker details
            String likerUsername = SecurityUtils.getCurrentUserLogin()
                    .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));
            User liker = userService.getUserByEmail(likerUsername);
            Long likerUserId = liker.getId();

            // Check for self-like
            if (Objects.equals(likerUserId, authorUserId)) {
                log.info("User {} liked their own element comment {}, no notification sent.", likerUserId, comment.getId());
                throw new AppException(ErrorCode.CAN_NOT_SELF_LIKE);
            } else {
            	updatedComment = commentElementRepository.save(comment);
                String elementName = updatedComment.getElement() != null ? updatedComment.getElement().getName() : "Unknown Element";
                String message = String.format("%s liked your comment on element '%s'.", liker.getName(), elementName);
                String notificationType = "COMMENT_LIKE_ELEMENT";

                // 1. Save notification to DB
                // related id is element id
                Long elementId = updatedComment.getElement().getId();
                notificationService.createAndSaveNotification(authorUserId, notificationType, message, elementId);

                // 2. Prepare payload for WebSocket
                NotificationPayload payload = NotificationPayload.builder()
                        .type(notificationType)
                        .message(message)
                        .relatedId(elementId)
                        .timestamp(Instant.now().atOffset(ZoneOffset.UTC).format(ISO_FORMATTER))
                        .build();

                // 3. Publish via WebSocket
                notificationPublisher.publishNotificationToUser(author.getEmail(), payload);
                log.info("Sent COMMENT_LIKE notification to author {} for element comment {} liked by user {}", authorUserId, updatedComment.getId(), likerUserId);
            }

        } catch (Exception e) {
            log.error("Error occurred during notification process for element comment like (commentId: {}): {}", comment.getId(), e.getMessage(), e);
            // Do not re-throw, the like itself was successful
        }
        // --- End Notification Logic ---

        LikeCommentElementResponse response = commentElementMapper.commentElementToLikeCommentElementResponse(updatedComment);
        log.info("End: Function like element comment id {} success", id);
        return response;
    }
    
    private CommentElement findCommentById(Long id) {
        return commentElementRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));
    }
} 