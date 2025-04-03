package com.periodic.backend.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.periodic.backend.domain.entity.User;
import com.periodic.backend.domain.request.comment.podcast.CreateCommentPodcastRequest;
import com.periodic.backend.domain.request.comment.podcast.UpdateCommentPodcastRequest;
import com.periodic.backend.domain.response.comment.podcast.CreateCommentPodcastResponse;
import com.periodic.backend.domain.response.comment.podcast.GetCommentPodcastResponse;
import com.periodic.backend.domain.response.comment.podcast.LikeCommentPodcastResponse;
import com.periodic.backend.domain.response.comment.podcast.ToggleActiveCommentPodcastResponse;
import com.periodic.backend.domain.response.comment.podcast.UpdateCommentPodcastResponse;
import com.periodic.backend.domain.response.pagination.Meta;
import com.periodic.backend.domain.response.pagination.PaginationResponse;
import com.periodic.backend.exception.AppException;
import com.periodic.backend.service.CommentPodcastService;
import com.periodic.backend.util.constant.ErrorCode;

@WebMvcTest(CommentPodcastController.class)
public class CommentPodcastControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentPodcastService commentPodcastService;

    @Autowired
    private ObjectMapper objectMapper;

    private CreateCommentPodcastRequest createCommentPodcastRequest;
    private CreateCommentPodcastResponse createCommentPodcastResponse;
    private UpdateCommentPodcastRequest updateCommentPodcastRequest;
    private UpdateCommentPodcastResponse updateCommentPodcastResponse;
    private GetCommentPodcastResponse getCommentPodcastResponse;
    private PaginationResponse<List<GetCommentPodcastResponse>> paginationResponse;
    private ToggleActiveCommentPodcastResponse toggleActiveResponse;
    private LikeCommentPodcastResponse likeResponse;

    @BeforeEach
    void setUp() {
        // Setup test data
        createCommentPodcastRequest = CreateCommentPodcastRequest.builder()
                .podcastId(120L)
                .content("This is a great podcast about hydrogen!")
                .build();

        createCommentPodcastResponse = CreateCommentPodcastResponse.builder()
                .id(120L)
                .podcastTitle("The story Hydrogen")
                .userName("tuine08@gmail.com")
                .userAvatar(null)
                .content("This is a great podcast about hydrogen!")
                .likes(0)
                .createdAt(Instant.now())
                .active(true)
                .build();

        updateCommentPodcastRequest = UpdateCommentPodcastRequest.builder()
                .content("Updated comment about hydrogen podcast")
                .build();

        updateCommentPodcastResponse = UpdateCommentPodcastResponse.builder()
        		.id(120L)
                .podcastTitle("The story Hydrogen")
                .userName("tuine08@gmail.com")
                .userAvatar(null)
                .content("Updated comment about hydrogen podcast")
                .likes(0)
                .createdAt(Instant.now())
                .active(true)
                .build();

        getCommentPodcastResponse = GetCommentPodcastResponse.builder()
        		.id(120L)
                .podcastTitle("The story Hydrogen")
                .userName("tuine08@gmail.com")
                .userAvatar(null)
                .content("This is a great podcast about hydrogen!")
                .likes(0)
                .createdAt(Instant.now())
                .active(true)
                .build();

        List<GetCommentPodcastResponse> commentList = new ArrayList<>();
        commentList.add(getCommentPodcastResponse);

        Meta meta = Meta.builder()
                .current(1)
                .pageSize(10)
                .totalPages(1)
                .totalItems(1L)
                .build();

        paginationResponse = new PaginationResponse<>(meta, commentList);

        toggleActiveResponse = ToggleActiveCommentPodcastResponse.builder()
                .id(1L)
                .active(true)
                .build();

        likeResponse = LikeCommentPodcastResponse.builder()
                .id(1L)
                .likes(1)
                .build();
    }

    @Test
    @WithMockUser(username = "tuine08@gmail.com")
    void createComment_shouldReturnCreatedComment() throws Exception {
        when(commentPodcastService.createComment(any(CreateCommentPodcastRequest.class), anyString()))
                .thenReturn(createCommentPodcastResponse);

        mockMvc.perform(post("/api/v1/comments/podcasts").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createCommentPodcastRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(createCommentPodcastResponse.getId()))
                .andExpect(jsonPath("$.data.podcastTitle").value(createCommentPodcastResponse.getPodcastTitle()))
                .andExpect(jsonPath("$.data.userName").value(createCommentPodcastResponse.getUserName()))
                .andExpect(jsonPath("$.data.content").value(createCommentPodcastResponse.getContent()))
                .andExpect(jsonPath("$.data.likes").value(createCommentPodcastResponse.getLikes()))
                .andExpect(jsonPath("$.data.active").value(createCommentPodcastResponse.isActive()));
    }

    @Test
    @WithMockUser
    void getComments_shouldReturnPaginatedComments() throws Exception {
        when(commentPodcastService.getComments(any(Pageable.class), anyString(), any(), any()))
                .thenReturn(paginationResponse);

        mockMvc.perform(get("/api/v1/comments/podcasts")
                .param("current", "1")
                .param("pageSize", "10")
                .param("term", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.meta.current").value(1))
                .andExpect(jsonPath("$.data.meta.pageSize").value(10))
                .andExpect(jsonPath("$.data.meta.totalItems").value(1))
                .andExpect(jsonPath("$.data.meta.totalPages").value(1))
                .andExpect(jsonPath("$.data.result[0].id").value(getCommentPodcastResponse.getId()))
                .andExpect(jsonPath("$.data.result[0].podcastTitle").value(getCommentPodcastResponse.getPodcastTitle()))
                .andExpect(jsonPath("$.data.result[0].content").value(getCommentPodcastResponse.getContent()));
    }

    @Test
    @WithMockUser
    void getComment_shouldReturnComment() throws Exception {
        when(commentPodcastService.getComment(anyLong()))
                .thenReturn(getCommentPodcastResponse);

        mockMvc.perform(get("/api/v1/comments/podcasts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(getCommentPodcastResponse.getId()))
                .andExpect(jsonPath("$.data.podcastTitle").value(getCommentPodcastResponse.getPodcastTitle()))
                .andExpect(jsonPath("$.data.userName").value(getCommentPodcastResponse.getUserName()))
                .andExpect(jsonPath("$.data.content").value(getCommentPodcastResponse.getContent()))
                .andExpect(jsonPath("$.data.likes").value(getCommentPodcastResponse.getLikes()))
                .andExpect(jsonPath("$.data.active").value(getCommentPodcastResponse.isActive()))
                .andExpect(jsonPath("$.data.userAvatar").value(getCommentPodcastResponse.getUserAvatar()));
    }

    @Test
    @WithMockUser(username = "tuine08@gmail.com")
    void updateComment_shouldReturnUpdatedComment() throws Exception {
        when(commentPodcastService.updateComment(eq(1L), any(UpdateCommentPodcastRequest.class), anyString()))
                .thenReturn(updateCommentPodcastResponse);

        mockMvc.perform(put("/api/v1/comments/podcasts/1").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateCommentPodcastRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(updateCommentPodcastResponse.getId()))
                .andExpect(jsonPath("$.data.content").value(updateCommentPodcastResponse.getContent()));
    }

    @Test
    @WithMockUser(username = "tuine08@gmail.com", roles = "ADMIN")
    void toggleActive_shouldReturnToggleResponse() throws Exception {
        when(commentPodcastService.toggleActive(anyLong()))
                .thenReturn(toggleActiveResponse);

        mockMvc.perform(patch("/api/v1/comments/podcasts/1/toggle-active").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(toggleActiveResponse.getId()))
                .andExpect(jsonPath("$.data.active").value(toggleActiveResponse.isActive()));
    }

    @Test
    @WithMockUser
    void likeComment_shouldReturnLikeResponse() throws Exception {
        when(commentPodcastService.likeComment(anyLong()))
                .thenReturn(likeResponse);

        mockMvc.perform(patch("/api/v1/comments/podcasts/1/like").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(likeResponse.getId()))
                .andExpect(jsonPath("$.data.likes").value(likeResponse.getLikes()));
    }

    @Test
    void createComment_withoutAuthentication_shouldReturnForbidden() throws Exception {
        mockMvc.perform(post("/api/v1/comments/podcasts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createCommentPodcastRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "tuine09@gmail.com")
    void updateComment_withoutAuthentication_shouldReturnForbidden() throws Exception {
    	when(commentPodcastService.updateComment(eq(1L), any(UpdateCommentPodcastRequest.class), anyString()))
    	.thenThrow(new AppException(ErrorCode.NOT_AUTHORIZED));
    	mockMvc.perform(put("/api/v1/comments/podcasts/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateCommentPodcastRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void toggleActive_withoutAdminRole_shouldReturnForbidden() throws Exception {
        mockMvc.perform(patch("/api/v1/comments/podcasts/1/toggle-active"))
                .andExpect(status().isForbidden());
    }
} 