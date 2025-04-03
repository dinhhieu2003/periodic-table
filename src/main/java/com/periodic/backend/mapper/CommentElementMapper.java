package com.periodic.backend.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;

import com.periodic.backend.domain.entity.CommentElement;
import com.periodic.backend.domain.request.comment.element.CreateCommentElementRequest;
import com.periodic.backend.domain.request.comment.element.UpdateCommentElementRequest;
import com.periodic.backend.domain.response.comment.element.CreateCommentElementResponse;
import com.periodic.backend.domain.response.comment.element.GetCommentElementResponse;
import com.periodic.backend.domain.response.comment.element.LikeCommentElementResponse;
import com.periodic.backend.domain.response.comment.element.ToggleActiveCommentElementResponse;
import com.periodic.backend.domain.response.comment.element.UpdateCommentElementResponse;

@Component
@Mapper(componentModel = "spring")
public interface CommentElementMapper {
    CommentElementMapper INSTANCE = Mappers.getMapper(CommentElementMapper.class);
    
    @Mapping(target = "element", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "likes", constant = "0")
    CommentElement createCommentElementRequestToCommentElement(CreateCommentElementRequest createCommentElementRequest);
    
    @Mapping(source = "element.name", target = "elementName")
    @Mapping(source = "user.name", target = "userName")
    @Mapping(source = "user.avatar", target = "userAvatar")
    CreateCommentElementResponse commentElementToCreateCommentElementResponse(CommentElement commentElement);
    
    @Mapping(source = "element.name", target = "elementName")
    @Mapping(source = "user.name", target = "userName")
    @Mapping(source = "user.avatar", target = "userAvatar")
    GetCommentElementResponse commentElementToGetCommentElementResponse(CommentElement commentElement);
    
    @Mapping(source = "element.name", target = "elementName")
    @Mapping(source = "user.name", target = "userName")
    @Mapping(source = "user.avatar", target = "userAvatar")
    UpdateCommentElementResponse commentElementToUpdateCommentElementResponse(CommentElement commentElement);
    
    default Page<GetCommentElementResponse> pageCommentElementToPageGetCommentElementResponse(Page<CommentElement> pageCommentElement) {
        List<GetCommentElementResponse> content = pageCommentElement.getContent()
                .stream()
                .map(this::commentElementToGetCommentElementResponse)
                .collect(Collectors.toList());
        return new PageImpl<>(content, pageCommentElement.getPageable(), pageCommentElement.getTotalElements());
    }
    
    default ToggleActiveCommentElementResponse commentElementToToggleActiveCommentElementResponse(CommentElement commentElement) {
        return new ToggleActiveCommentElementResponse(commentElement.getId(), commentElement.isActive());
    }
    
    default LikeCommentElementResponse commentElementToLikeCommentElementResponse(CommentElement commentElement) {
        return new LikeCommentElementResponse(commentElement.getId(), commentElement.getLikes());
    }
} 