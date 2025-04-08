package com.periodic.backend.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.periodic.backend.domain.request.podcast.CreatePodcastRequest;
import com.periodic.backend.domain.response.podcast.CreatePodcastResponse;
import com.periodic.backend.domain.response.podcast.GetPodcastResponse;
import com.periodic.backend.domain.response.pagination.Meta;
import com.periodic.backend.domain.response.pagination.PaginationResponse;
import com.periodic.backend.service.PodcastService;
import com.periodic.backend.util.constant.SortParam;

@WebMvcTest(PodcastController.class)
public class PodcastControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	
	@MockBean
	private PodcastService podcastService;
	
	private CreatePodcastRequest createPodcastRequest;
    private CreatePodcastResponse createPodcastResponse;
    private PaginationResponse<List<GetPodcastResponse>> paginationResponse;
    private GetPodcastResponse getPodcastResponse;
    
    @BeforeEach
    void setup() {
    	createPodcastRequest = CreatePodcastRequest.builder()
    			.title("Hydrogen: The First Element")
    		    .transcript("A deep dive into the properties and history of hydrogen")
    		    .audioUrl("https://example.com/hydrogen-podcast.mp3")
    		    .elementId(1L)
    		    .build();
    	
    	createPodcastResponse = CreatePodcastResponse.builder()
    			.id(1L)
    			.title("Hydrogen: The First Element")
    		    .transcript("A deep dive into the properties and history of hydrogen")
    		    .audioUrl("https://example.com/hydrogen-podcast.mp3")
    		    .element("Hydrogen")
    		    .build();
    	
    	List<GetPodcastResponse> podcasts = new ArrayList<>();
        GetPodcastResponse podcast = GetPodcastResponse.builder()
        		.id(1L)
    			.title("Hydrogen: The First Element")
    		    .transcript("A deep dive into the properties and history of hydrogen")
    		    .audioUrl("https://example.com/hydrogen-podcast.mp3")
    		    .element("Hydrogen")
    		    .build();
        podcasts.add(podcast);
        
        Meta meta = Meta.builder()
        		.current(1)
        		.pageSize(10)
        		.totalItems(1)
        		.totalPages(1).build();
        paginationResponse = new PaginationResponse<>();
        paginationResponse.setMeta(meta);
        paginationResponse.setResult(podcasts);
        
        getPodcastResponse = GetPodcastResponse.builder()
        		.id(1L)
    			.title("Hydrogen: The First Element")
    		    .transcript("A deep dive into the properties and history of hydrogen")
    		    .audioUrl("https://example.com/hydrogen-podcast.mp3")
    		    .element("Hydrogen")
    		    .build();
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void createPodcast_withValidRequest_shouldReturnCreatedPodcast() throws Exception {
        when(podcastService.createPodcast(any(CreatePodcastRequest.class))).thenReturn(createPodcastResponse);

        mockMvc.perform(post("/api/v1/podcasts").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createPodcastRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.title").value("Hydrogen: The First Element"))
                .andExpect(jsonPath("$.data.element").value("Hydrogen"));
    }
    
    @Test
    @WithMockUser(roles = "USER")
    void createPodcast_withoutAdminRole_shouldReturnForbidden() throws Exception {
        mockMvc.perform(post("/api/v1/podcasts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createPodcastRequest)))
                .andExpect(status().isForbidden());
    }
    
    @Test
    @WithMockUser
    void getPodcasts_withSearchAndSort_shouldReturnPagedPodcasts() throws Exception {
        when(podcastService.getPodcasts(any(Pageable.class), anyString(), any(String[].class), any(String[].class), any(Boolean.class)))
                .thenReturn(paginationResponse);

        mockMvc.perform(get("/api/v1/podcasts")
                .param("current", "1")
                .param("pageSize", "10")
                .param("term", "hydrogen")
                .param("sortBy", "title")
                .param("sortBy", "element.name")
                .param("sortDirection", "asc")
                .param("sortDirection", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.result[0].title").value("Hydrogen: The First Element"))
                .andExpect(jsonPath("$.data.meta.current").value(1))
                .andExpect(jsonPath("$.data.meta.totalPages").value(1))
                .andExpect(jsonPath("$.data.meta.totalItems").value(1));
    }
    
    @Test
    @WithMockUser
    void getPodcasts_withActiveFilter_shouldReturnFilteredPodcasts() throws Exception {
        when(podcastService.getPodcasts(any(Pageable.class), anyString(), any(String[].class), any(String[].class), eq(true)))
                .thenReturn(paginationResponse);

        mockMvc.perform(get("/api/v1/podcasts")
                .param("current", "1")
                .param("pageSize", "10")
                .param("active", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.result[0].title").value("Hydrogen: The First Element"));
    }
    
    @Test
    @WithMockUser
    void getPodcasts_withDefaultSort_shouldReturnSortedPodcasts() throws Exception {
        when(podcastService.getPodcasts(any(Pageable.class), anyString(), any(String[].class), any(String[].class), any(Boolean.class)))
                .thenReturn(paginationResponse);

        mockMvc.perform(get("/api/v1/podcasts")
                .param("current", "1")
                .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.result[0].title").value("Hydrogen: The First Element"));
    }
    
    @Test
    @WithMockUser
    void getPodcasts_withInvalidSortDirection_shouldUseDefault() throws Exception {
        when(podcastService.getPodcasts(any(Pageable.class), anyString(), any(String[].class), any(String[].class), any(Boolean.class)))
                .thenReturn(paginationResponse);

        mockMvc.perform(get("/api/v1/podcasts")
                .param("current", "1")
                .param("pageSize", "10")
                .param("sortDirection", "invalid"))
                .andExpect(status().isOk());
    }
    
    @Test
    @WithMockUser
    void getPodcast_shouldReturnPodcast() throws Exception {
    	when(podcastService.getPodcast(1L)).thenReturn(getPodcastResponse);
    	
    	mockMvc.perform(get("/api/v1/podcasts/{id}", 1L)
    		.contentType(MediaType.APPLICATION_JSON))
    	
    		.andExpect(status().isOk())
            .andExpect(jsonPath("data.id").value(1))
            .andExpect(jsonPath("data.title").value("Hydrogen: The First Element"))
            .andExpect(jsonPath("data.elementId").value(1));
    }
    
    @Test
    @WithMockUser
    void getPodcastsByElementId_shouldReturnPodcasts() throws Exception {
        when(podcastService.getPodcastsByElementId(any(Pageable.class), eq(1L)))
                .thenReturn(paginationResponse);

        mockMvc.perform(get("/api/v1/podcasts/by-element/{elementId}", 1L)
                .param("current", "1")
                .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.result[0].title").value("Hydrogen: The First Element"))
                .andExpect(jsonPath("$.data.meta.current").value(1))
                .andExpect(jsonPath("$.data.meta.totalPages").value(1))
                .andExpect(jsonPath("$.data.meta.totalItems").value(1));
    }
}
