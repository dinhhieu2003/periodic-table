package com.periodic.backend.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.periodic.backend.domain.request.podcast.CreatePodcastRequest;
import com.periodic.backend.domain.response.pagination.Meta;
import com.periodic.backend.domain.response.pagination.PaginationResponse;
import com.periodic.backend.domain.response.podcast.CreatePodcastResponse;
import com.periodic.backend.domain.response.podcast.GetPodcastResponse;
import com.periodic.backend.service.PodcastService;

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
	private GetPodcastResponse getPodcastResponse;
	private PaginationResponse<List<GetPodcastResponse>> paginationResponse;
	
	@BeforeEach
    void setup() {
		createPodcastRequest = CreatePodcastRequest.builder()
				.title("Podcast about hidro")
				.audioUrl("http://audio.mp3")
				.transcript("Hidro is an element")
				.elementId(1L)
				.build();
		createPodcastResponse = CreatePodcastResponse.builder()
				.id(1L)
				.title("Podcast about hidro")
				.audioUrl("http://audio.mp3")
				.transcript("Hidro is an element")
				.element("Hydrogen")
				.build();
		getPodcastResponse = GetPodcastResponse.builder()
				.id(1L)
				.title("Podcast about hidro")
				.audioUrl("http://audio.mp3")
				.transcript("Hidro is an element")
				.element("Hydrogen")
				.build();
		paginationResponse = new PaginationResponse<>();
		Meta meta = Meta.builder()
        		.current(1)
        		.pageSize(10)
        		.totalItems(1)
        		.totalPages(1).build();
		paginationResponse.setMeta(meta);
		paginationResponse.setResult(Collections.singletonList(getPodcastResponse));
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
                .andExpect(jsonPath("$.data.element").value("Hydrogen"))
                .andExpect(jsonPath("$.data.audioUrl").value("http://audio.mp3"));
	}

	@Test
	@WithMockUser
	void getPodcastsByElementId_shouldReturnPaginatedPodcasts() throws Exception {
		when(podcastService.getPodcastsByElementId(any(Pageable.class), eq(1L)))
				.thenReturn(paginationResponse);

		mockMvc.perform(get("/api/v1/podcasts/by-element/1").with(csrf())
				.param("current", "1")
				.param("pageSize", "10"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.meta.current").value(1))
				.andExpect(jsonPath("$.data.meta.pageSize").value(10))
				.andExpect(jsonPath("$.data.meta.totalPages").value(1))
				.andExpect(jsonPath("$.data.result[0].id").value(getPodcastResponse.getId()))
				.andExpect(jsonPath("$.data.result[0].title").value(getPodcastResponse.getTitle()));
	}
}
