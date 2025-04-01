package com.periodic.backend.controller;

import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.periodic.backend.domain.request.podcast.CreatePodcastRequest;
import com.periodic.backend.domain.response.podcast.CreatePodcastResponse;
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
}
