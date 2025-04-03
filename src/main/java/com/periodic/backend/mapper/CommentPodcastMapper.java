package com.periodic.backend.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;

import com.periodic.backend.domain.entity.CommentPodcast;
import com.periodic.backend.domain.request.comment.podcast.CreateCommentPodcastRequest;
import com.periodic.backend.domain.response.comment.podcast.CreateCommentPodcastResponse;
import com.periodic.backend.domain.response.comment.podcast.GetCommentPodcastResponse;
import com.periodic.backend.domain.response.comment.podcast.LikeCommentPodcastResponse;
import com.periodic.backend.domain.response.comment.podcast.ToggleActiveCommentPodcastResponse;
import com.periodic.backend.domain.response.comment.podcast.UpdateCommentPodcastResponse;

@Component
@Mapper(componentModel = "spring")
public interface CommentPodcastMapper {
    CommentPodcastMapper INSTANCE = Mappers.getMapper(CommentPodcastMapper.class);
    
    @Mapping(target = "podcast", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "likes", constant = "0")
    CommentPodcast createCommentPodcastRequestToCommentPodcast(CreateCommentPodcastRequest createCommentPodcastRequest);
    
    @Mapping(source = "podcast.title", target = "podcastTitle")
    @Mapping(source = "user.name", target = "userName")
    @Mapping(source = "user.avatar", target = "userAvatar")
    CreateCommentPodcastResponse commentPodcastToCreateCommentPodcastResponse(CommentPodcast commentPodcast);
    
    @Mapping(source = "podcast.title", target = "podcastTitle")
    @Mapping(source = "user.name", target = "userName")
    @Mapping(source = "user.avatar", target = "userAvatar")
    GetCommentPodcastResponse commentPodcastToGetCommentPodcastResponse(CommentPodcast commentPodcast);
    
    @Mapping(source = "podcast.title", target = "podcastTitle")
    @Mapping(source = "user.name", target = "userName")
    @Mapping(source = "user.avatar", target = "userAvatar")
    UpdateCommentPodcastResponse commentPodcastToUpdateCommentPodcastResponse(CommentPodcast commentPodcast);
    
    default Page<GetCommentPodcastResponse> pageCommentPodcastToPageGetCommentPodcastResponse(Page<CommentPodcast> pageCommentPodcast) {
        List<GetCommentPodcastResponse> content = pageCommentPodcast.getContent()
                .stream()
                .map(this::commentPodcastToGetCommentPodcastResponse)
                .collect(Collectors.toList());
        return new PageImpl<>(content, pageCommentPodcast.getPageable(), pageCommentPodcast.getTotalElements());
    }
    
    default ToggleActiveCommentPodcastResponse commentPodcastToToggleActiveCommentPodcastResponse(CommentPodcast commentPodcast) {
        return new ToggleActiveCommentPodcastResponse(commentPodcast.getId(), commentPodcast.isActive());
    }
    
    default LikeCommentPodcastResponse commentPodcastToLikeCommentPodcastResponse(CommentPodcast commentPodcast) {
        return new LikeCommentPodcastResponse(commentPodcast.getId(), commentPodcast.getLikes());
    }
} 