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
import com.periodic.backend.domain.request.comment.element.CreateCommentElementRequest;
import com.periodic.backend.domain.request.comment.element.UpdateCommentElementRequest;
import com.periodic.backend.domain.response.comment.element.CreateCommentElementResponse;
import com.periodic.backend.domain.response.comment.element.GetCommentElementResponse;
import com.periodic.backend.domain.response.comment.element.LikeCommentElementResponse;
import com.periodic.backend.domain.response.comment.element.ToggleActiveCommentElementResponse;
import com.periodic.backend.domain.response.comment.element.UpdateCommentElementResponse;
import com.periodic.backend.domain.response.pagination.Meta;
import com.periodic.backend.domain.response.pagination.PaginationResponse;
import com.periodic.backend.exception.AppException;
import com.periodic.backend.service.CommentElementService;
import com.periodic.backend.util.constant.ErrorCode;

@WebMvcTest(CommentElementController.class)
public class CommentElementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentElementService commentElementService;

    @Autowired
    private ObjectMapper objectMapper;

    private CreateCommentElementRequest createCommentElementRequest;
    private CreateCommentElementResponse createCommentElementResponse;
    private UpdateCommentElementRequest updateCommentElementRequest;
    private UpdateCommentElementResponse updateCommentElementResponse;
    private GetCommentElementResponse getCommentElementResponse;
    private PaginationResponse<List<GetCommentElementResponse>> paginationResponse;
    private ToggleActiveCommentElementResponse toggleActiveResponse;
    private LikeCommentElementResponse likeResponse;

    @BeforeEach
    void setUp() {
        // Setup test data
        createCommentElementRequest = CreateCommentElementRequest.builder()
                .elementId(1L)
                .content("This is a very interesting element!")
                .build();

        createCommentElementResponse = CreateCommentElementResponse.builder()
                .id(1L)
                .elementName("Hydrogen")
                .userName("tuine08@gmail.com")
                .userAvatar(null)
                .content("This is a very interesting element!")
                .likes(0)
                .createdAt(Instant.now())
                .active(true)
                .build();

        updateCommentElementRequest = UpdateCommentElementRequest.builder()
                .content("Updated comment about hydrogen")
                .build();

        updateCommentElementResponse = UpdateCommentElementResponse.builder()
                .id(1L)
                .elementName("Hydrogen")
                .userName("tuine08@gmail.com")
                .userAvatar(null)
                .content("Updated comment about hydrogen")
                .likes(0)
                .createdAt(Instant.now())
                .active(true)
                .build();

        getCommentElementResponse = GetCommentElementResponse.builder()
                .id(1L)
                .elementName("Hydrogen")
                .userName("tuine08@gmail.com")
                .userAvatar(null)
                .content("This is a very interesting element!")
                .likes(0)
                .createdAt(Instant.now())
                .active(true)
                .build();

        List<GetCommentElementResponse> commentList = new ArrayList<>();
        commentList.add(getCommentElementResponse);

        Meta meta = Meta.builder()
                .current(1)
                .pageSize(10)
                .totalPages(1)
                .totalItems(1L)
                .build();

        paginationResponse = new PaginationResponse<>(meta, commentList);

        toggleActiveResponse = ToggleActiveCommentElementResponse.builder()
                .id(1L)
                .active(false)
                .build();

        likeResponse = LikeCommentElementResponse.builder()
                .id(1L)
                .likes(1)
                .build();
    }

    @Test
    @WithMockUser(username = "tuine08@gmail.com")
    void createComment_shouldReturnCreatedComment() throws Exception {
        when(commentElementService.createComment(any(CreateCommentElementRequest.class), anyString()))
                .thenReturn(createCommentElementResponse);

        mockMvc.perform(post("/api/v1/comments/elements").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createCommentElementRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(createCommentElementResponse.getId()))
                .andExpect(jsonPath("$.data.elementName").value(createCommentElementResponse.getElementName()))
                .andExpect(jsonPath("$.data.userName").value(createCommentElementResponse.getUserName()))
                .andExpect(jsonPath("$.data.content").value(createCommentElementResponse.getContent()))
                .andExpect(jsonPath("$.data.likes").value(createCommentElementResponse.getLikes()))
                .andExpect(jsonPath("$.data.active").value(createCommentElementResponse.isActive()));
    }

    @Test
    @WithMockUser
    void getComments_shouldReturnPaginatedComments() throws Exception {
        when(commentElementService.getComments(any(Pageable.class), anyString(), any(), any()))
                .thenReturn(paginationResponse);

        mockMvc.perform(get("/api/v1/comments/elements")
                .param("current", "1")
                .param("pageSize", "10")
                .param("term", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.meta.current").value(1))
                .andExpect(jsonPath("$.data.meta.pageSize").value(10))
                .andExpect(jsonPath("$.data.meta.totalItems").value(1))
                .andExpect(jsonPath("$.data.meta.totalPages").value(1))
                .andExpect(jsonPath("$.data.result[0].id").value(getCommentElementResponse.getId()))
                .andExpect(jsonPath("$.data.result[0].elementName").value(getCommentElementResponse.getElementName()))
                .andExpect(jsonPath("$.data.result[0].content").value(getCommentElementResponse.getContent()));
    }

    @Test
    @WithMockUser
    void getComment_shouldReturnComment() throws Exception {
        when(commentElementService.getComment(anyLong()))
                .thenReturn(getCommentElementResponse);

        mockMvc.perform(get("/api/v1/comments/elements/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(getCommentElementResponse.getId()))
                .andExpect(jsonPath("$.data.elementName").value(getCommentElementResponse.getElementName()))
                .andExpect(jsonPath("$.data.userName").value(getCommentElementResponse.getUserName()))
                .andExpect(jsonPath("$.data.content").value(getCommentElementResponse.getContent()))
                .andExpect(jsonPath("$.data.likes").value(getCommentElementResponse.getLikes()))
                .andExpect(jsonPath("$.data.active").value(getCommentElementResponse.isActive()))
                .andExpect(jsonPath("$.data.userAvatar").value(getCommentElementResponse.getUserAvatar()));
    }

    @Test
    @WithMockUser(username = "tuine08@gmail.com")
    void updateComment_shouldReturnUpdatedComment() throws Exception {
        when(commentElementService.updateComment(eq(1L), any(UpdateCommentElementRequest.class), anyString()))
                .thenReturn(updateCommentElementResponse);

        mockMvc.perform(put("/api/v1/comments/elements/1").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateCommentElementRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(updateCommentElementResponse.getId()))
                .andExpect(jsonPath("$.data.content").value(updateCommentElementResponse.getContent()));
    }

    @Test
    @WithMockUser(username = "tuine08@gmail.com", roles = "ADMIN")
    void toggleActive_shouldReturnToggleResponse() throws Exception {
        when(commentElementService.toggleActive(anyLong()))
                .thenReturn(toggleActiveResponse);

        mockMvc.perform(patch("/api/v1/comments/elements/1/toggle-active").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(toggleActiveResponse.getId()))
                .andExpect(jsonPath("$.data.active").value(toggleActiveResponse.isActive()));
    }

    @Test
    @WithMockUser
    void likeComment_shouldReturnLikeResponse() throws Exception {
        when(commentElementService.likeComment(anyLong()))
                .thenReturn(likeResponse);

        mockMvc.perform(patch("/api/v1/comments/elements/1/like").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(likeResponse.getId()))
                .andExpect(jsonPath("$.data.likes").value(likeResponse.getLikes()));
    }

    @Test
    void createComment_withoutAuthentication_shouldReturnForbidden() throws Exception {
        mockMvc.perform(post("/api/v1/comments/elements")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createCommentElementRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "tuine09@gmail.com")
    void updateComment_withoutAuthentication_shouldReturnForbidden() throws Exception {
    	when(commentElementService.updateComment(eq(1L), any(UpdateCommentElementRequest.class), anyString()))
        .thenThrow(new AppException(ErrorCode.NOT_AUTHORIZED));
    	mockMvc.perform(put("/api/v1/comments/elements/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateCommentElementRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void toggleActive_withoutAdminRole_shouldReturnForbidden() throws Exception {
        mockMvc.perform(patch("/api/v1/comments/elements/1/toggle-active"))
                .andExpect(status().isForbidden());
    }
} 