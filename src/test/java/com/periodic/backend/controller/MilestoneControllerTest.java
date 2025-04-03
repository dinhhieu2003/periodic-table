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
import com.periodic.backend.domain.request.milestone.CreateMilestoneRequest;
import com.periodic.backend.domain.request.milestone.UpdateMilestoneRequest;
import com.periodic.backend.domain.response.milestone.CreateMilestoneResponse;
import com.periodic.backend.domain.response.milestone.GetMilestoneResponse;
import com.periodic.backend.domain.response.milestone.ToggleActiveMilestoneResponse;
import com.periodic.backend.domain.response.milestone.UpdateMilestoneResponse;
import com.periodic.backend.domain.response.pagination.Meta;
import com.periodic.backend.domain.response.pagination.PaginationResponse;
import com.periodic.backend.service.MilestoneService;

@WebMvcTest(MilestoneController.class)
public class MilestoneControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MilestoneService milestoneService;

    @Autowired
    private ObjectMapper objectMapper;

    private CreateMilestoneRequest createMilestoneRequest;
    private CreateMilestoneResponse createMilestoneResponse;
    private UpdateMilestoneRequest updateMilestoneRequest;
    private UpdateMilestoneResponse updateMilestoneResponse;
    private GetMilestoneResponse getMilestoneResponse;
    private PaginationResponse<List<GetMilestoneResponse>> paginationResponse;
    private ToggleActiveMilestoneResponse toggleActiveResponse;

    @BeforeEach
    void setUp() {
        // Setup test data
        createMilestoneRequest = CreateMilestoneRequest.builder()
                .scientistId(1L)
                .year(1903)
                .milestone("Nobel Prize in Physics")
                .details("Awarded the Nobel Prize in Physics for her research on radiation phenomena")
                .build();

        createMilestoneResponse = CreateMilestoneResponse.builder()
                .id(1L)
                .scientistName("Vương Đình Hiếu")
                .year(1903)
                .milestone("Nobel Prize in Physics")
                .details("Awarded the Nobel Prize in Physics for her research on radiation phenomena")
                .build();

        updateMilestoneRequest = UpdateMilestoneRequest.builder()
                .scientistId(1L)
                .year(1911)
                .milestone("Nobel Prize in Chemistry")
                .details("Awarded the Nobel Prize in Chemistry for her discovery of radium and polonium")
                .build();

        updateMilestoneResponse = UpdateMilestoneResponse.builder()
                .id(1L)
                .scientistName("Vương Đình Hiếu")
                .year(1911)
                .milestone("Nobel Prize in Chemistry")
                .details("Awarded the Nobel Prize in Chemistry for her discovery of radium and polonium")
                .build();

        getMilestoneResponse = GetMilestoneResponse.builder()
                .id(1L)
                .scientistName("Vương Đình Hiếu")
                .year(1903)
                .milestone("Nobel Prize in Physics")
                .details("Awarded the Nobel Prize in Physics for her research on radiation phenomena")
                .build();

        // Setup pagination response
        List<GetMilestoneResponse> milestones = new ArrayList<>();
        milestones.add(getMilestoneResponse);

        Meta meta = Meta.builder()
                .current(1)
                .pageSize(10)
                .totalItems(1)
                .totalPages(1)
                .build();

        paginationResponse = new PaginationResponse<>();
        paginationResponse.setMeta(meta);
        paginationResponse.setResult(milestones);

        // Setup toggle active response
        toggleActiveResponse = ToggleActiveMilestoneResponse.builder()
                .id(1L)
                .active(true)
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createMilestone_shouldReturnCreatedMilestone() throws Exception {
        when(milestoneService.createMilestone(any(CreateMilestoneRequest.class)))
                .thenReturn(createMilestoneResponse);

        mockMvc.perform(post("/api/v1/milestones").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createMilestoneRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(createMilestoneResponse.getId()))
                .andExpect(jsonPath("$.data.scientistName").value(createMilestoneResponse.getScientistName()))
                .andExpect(jsonPath("$.data.year").value(createMilestoneResponse.getYear()))
                .andExpect(jsonPath("$.data.milestone").value(createMilestoneResponse.getMilestone()))
                .andExpect(jsonPath("$.data.details").value(createMilestoneResponse.getDetails()))
                .andExpect(jsonPath("$.data.active").value(createMilestoneResponse.isActive()));
    }

    @Test
    @WithMockUser
    void getMilestones_shouldReturnPaginatedMilestones() throws Exception {
        when(milestoneService.getMilestones(any(Pageable.class), anyString(), anyString()))
                .thenReturn(paginationResponse);

        mockMvc.perform(get("/api/v1/milestones")
                .param("current", "1")
                .param("pageSize", "10")
                .param("term", "")
                .param("searchBy", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.meta.current").value(1))
                .andExpect(jsonPath("$.data.meta.pageSize").value(10))
                .andExpect(jsonPath("$.data.meta.totalItems").value(1))
                .andExpect(jsonPath("$.data.meta.totalPages").value(1))
                .andExpect(jsonPath("$.data.result[0].id").value(getMilestoneResponse.getId()))
                .andExpect(jsonPath("$.data.result[0].scientistName").value(getMilestoneResponse.getScientistName()))
                .andExpect(jsonPath("$.data.result[0].year").value(getMilestoneResponse.getYear()));
    }

    @Test
    @WithMockUser
    void getMilestone_shouldReturnMilestone() throws Exception {
        when(milestoneService.getMilestone(anyLong()))
                .thenReturn(getMilestoneResponse);

        mockMvc.perform(get("/api/v1/milestones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(getMilestoneResponse.getId()))
                .andExpect(jsonPath("$.data.scientistName").value(getMilestoneResponse.getScientistName()))
                .andExpect(jsonPath("$.data.year").value(getMilestoneResponse.getYear()))
                .andExpect(jsonPath("$.data.milestone").value(getMilestoneResponse.getMilestone()))
                .andExpect(jsonPath("$.data.details").value(getMilestoneResponse.getDetails()))
                .andExpect(jsonPath("$.data.active").value(getMilestoneResponse.isActive()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateMilestone_shouldReturnUpdatedMilestone() throws Exception {
        when(milestoneService.updateMilestone(eq(1L), any(UpdateMilestoneRequest.class)))
                .thenReturn(updateMilestoneResponse);

        mockMvc.perform(put("/api/v1/milestones/1").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateMilestoneRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(updateMilestoneResponse.getId()))
                .andExpect(jsonPath("$.data.scientistName").value(updateMilestoneResponse.getScientistName()))
                .andExpect(jsonPath("$.data.year").value(updateMilestoneResponse.getYear()))
                .andExpect(jsonPath("$.data.milestone").value(updateMilestoneResponse.getMilestone()))
                .andExpect(jsonPath("$.data.details").value(updateMilestoneResponse.getDetails()))
                .andExpect(jsonPath("$.data.active").value(updateMilestoneResponse.isActive()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void toggleActive_shouldReturnToggleResponse() throws Exception {
        when(milestoneService.toggleActive(anyLong()))
                .thenReturn(toggleActiveResponse);

        mockMvc.perform(patch("/api/v1/milestones/1/toggle-active").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(toggleActiveResponse.getId()))
                .andExpect(jsonPath("$.data.active").value(toggleActiveResponse.isActive()));
    }

    @Test
    @WithMockUser
    void createMilestone_withoutAdminRole_shouldReturnForbidden() throws Exception {
        mockMvc.perform(post("/api/v1/milestones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createMilestoneRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void updateMilestone_withoutAdminRole_shouldReturnForbidden() throws Exception {
        mockMvc.perform(put("/api/v1/milestones/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateMilestoneRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void toggleActive_withoutAdminRole_shouldReturnForbidden() throws Exception {
        mockMvc.perform(patch("/api/v1/milestones/1/toggle-active"))
                .andExpect(status().isForbidden());
    }
} 