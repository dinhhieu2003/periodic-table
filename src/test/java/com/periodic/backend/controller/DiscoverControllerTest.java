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
import com.periodic.backend.domain.request.discover.CreateDiscoverRequest;
import com.periodic.backend.domain.request.discover.UpdateDiscoverRequest;
import com.periodic.backend.domain.response.discover.CreateDiscoverResponse;
import com.periodic.backend.domain.response.discover.GetDiscoverResponse;
import com.periodic.backend.domain.response.discover.ToggleActiveDiscoverResponse;
import com.periodic.backend.domain.response.discover.UpdateDiscoverResponse;
import com.periodic.backend.domain.response.pagination.Meta;
import com.periodic.backend.domain.response.pagination.PaginationResponse;
import com.periodic.backend.service.DiscoverService;

@WebMvcTest(DiscoverController.class)
public class DiscoverControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DiscoverService discoverService;

    @Autowired
    private ObjectMapper objectMapper;

    private CreateDiscoverRequest createDiscoverRequest;
    private CreateDiscoverResponse createDiscoverResponse;
    private UpdateDiscoverRequest updateDiscoverRequest;
    private UpdateDiscoverResponse updateDiscoverResponse;
    private GetDiscoverResponse getDiscoverResponse;
    private PaginationResponse<List<GetDiscoverResponse>> paginationResponse;
    private ToggleActiveDiscoverResponse toggleActiveResponse;

    @BeforeEach
    void setUp() {
        // Setup test data
        createDiscoverRequest = CreateDiscoverRequest.builder()
                .scientistId(1L)
                .elementId(1L)
                .discoveryYear(1911)
                .discoveryLocation("Paris, France")
                .build();

        createDiscoverResponse = CreateDiscoverResponse.builder()
                .id(1L)
                .scientistName("Vương Đình Hiếu")
                .elementName("Hydrogen")
                .elementSymbol("H")
                .discoveryYear(1911)
                .discoveryLocation("Paris, France")
                .build();

        updateDiscoverRequest = UpdateDiscoverRequest.builder()
                .scientistId(1L)
                .elementId(1L)
                .discoveryYear(1910)
                .discoveryLocation("Warsaw, Poland")
                .build();

        updateDiscoverResponse = UpdateDiscoverResponse.builder()
                .id(1L)
                .scientistName("Vương Đình Hiếu")
                .elementName("Hydrogen")
                .elementSymbol("H")
                .discoveryYear(1910)
                .discoveryLocation("Warsaw, Poland")
                .build();

        getDiscoverResponse = GetDiscoverResponse.builder()
                .id(1L)
                .scientistName("Vương Đình Hiếu")
                .elementName("Hydrogen")
                .elementSymbol("H")
                .discoveryYear(1911)
                .discoveryLocation("Paris, France")
                .build();

        // Setup pagination response
        List<GetDiscoverResponse> discoveries = new ArrayList<>();
        discoveries.add(getDiscoverResponse);

        Meta meta = Meta.builder()
                .current(1)
                .pageSize(10)
                .totalItems(1)
                .totalPages(1)
                .build();

        paginationResponse = new PaginationResponse<>();
        paginationResponse.setMeta(meta);
        paginationResponse.setResult(discoveries);

        // Setup toggle active response
        toggleActiveResponse = ToggleActiveDiscoverResponse.builder()
                .id(1L)
                .active(true)
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createDiscover_shouldReturnCreatedDiscover() throws Exception {
        when(discoverService.createDiscover(any(CreateDiscoverRequest.class)))
                .thenReturn(createDiscoverResponse);

        mockMvc.perform(post("/api/v1/discoveries").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDiscoverRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(createDiscoverResponse.getId()))
                .andExpect(jsonPath("$.data.scientistName").value(createDiscoverResponse.getScientistName()))
                .andExpect(jsonPath("$.data.elementName").value(createDiscoverResponse.getElementName()))
                .andExpect(jsonPath("$.data.elementSymbol").value(createDiscoverResponse.getElementSymbol()))
                .andExpect(jsonPath("$.data.discoveryYear").value(createDiscoverResponse.getDiscoveryYear()))
                .andExpect(jsonPath("$.data.discoveryLocation").value(createDiscoverResponse.getDiscoveryLocation()))
                .andExpect(jsonPath("$.data.active").value(createDiscoverResponse.isActive()));
    }

    @Test
    @WithMockUser
    void getDiscoveries_shouldReturnPaginatedDiscoveries() throws Exception {
        when(discoverService.getDiscoveries(any(Pageable.class), anyString(), any(String[].class), any(String[].class), any(Boolean.class)))
                .thenReturn(paginationResponse);

        mockMvc.perform(get("/api/v1/discoveries")
                .param("current", "1")
                .param("pageSize", "10")
                .param("term", "")
                .param("searchBy", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.meta.current").value(1))
                .andExpect(jsonPath("$.data.meta.pageSize").value(10))
                .andExpect(jsonPath("$.data.meta.totalItems").value(1))
                .andExpect(jsonPath("$.data.meta.totalPages").value(1))
                .andExpect(jsonPath("$.data.result[0].id").value(getDiscoverResponse.getId()))
                .andExpect(jsonPath("$.data.result[0].scientistName").value(getDiscoverResponse.getScientistName()))
                .andExpect(jsonPath("$.data.result[0].elementName").value(getDiscoverResponse.getElementName()));
    }

    @Test
    @WithMockUser
    void getDiscover_shouldReturnDiscover() throws Exception {
        when(discoverService.getDiscover(anyLong()))
                .thenReturn(getDiscoverResponse);

        mockMvc.perform(get("/api/v1/discoveries/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(getDiscoverResponse.getId()))
                .andExpect(jsonPath("$.data.scientistName").value(getDiscoverResponse.getScientistName()))
                .andExpect(jsonPath("$.data.elementName").value(getDiscoverResponse.getElementName()))
                .andExpect(jsonPath("$.data.elementSymbol").value(getDiscoverResponse.getElementSymbol()))
                .andExpect(jsonPath("$.data.discoveryYear").value(getDiscoverResponse.getDiscoveryYear()))
                .andExpect(jsonPath("$.data.discoveryLocation").value(getDiscoverResponse.getDiscoveryLocation()))
                .andExpect(jsonPath("$.data.active").value(getDiscoverResponse.isActive()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateDiscover_shouldReturnUpdatedDiscover() throws Exception {
        when(discoverService.updateDiscover(eq(1L), any(UpdateDiscoverRequest.class)))
                .thenReturn(updateDiscoverResponse);

        mockMvc.perform(put("/api/v1/discoveries/1").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDiscoverRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(updateDiscoverResponse.getId()))
                .andExpect(jsonPath("$.data.scientistName").value(updateDiscoverResponse.getScientistName()))
                .andExpect(jsonPath("$.data.elementName").value(updateDiscoverResponse.getElementName()))
                .andExpect(jsonPath("$.data.elementSymbol").value(updateDiscoverResponse.getElementSymbol()))
                .andExpect(jsonPath("$.data.discoveryYear").value(updateDiscoverResponse.getDiscoveryYear()))
                .andExpect(jsonPath("$.data.discoveryLocation").value(updateDiscoverResponse.getDiscoveryLocation()))
                .andExpect(jsonPath("$.data.active").value(updateDiscoverResponse.isActive()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void toggleActive_shouldReturnToggleResponse() throws Exception {
        when(discoverService.toggleActive(anyLong()))
                .thenReturn(toggleActiveResponse);

        mockMvc.perform(patch("/api/v1/discoveries/1/toggle-active").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(toggleActiveResponse.getId()))
                .andExpect(jsonPath("$.data.active").value(toggleActiveResponse.isActive()));
    }

    @Test
    @WithMockUser
    void createDiscover_withoutAdminRole_shouldReturnForbidden() throws Exception {
        mockMvc.perform(post("/api/v1/discoveries")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDiscoverRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void updateDiscover_withoutAdminRole_shouldReturnForbidden() throws Exception {
        mockMvc.perform(put("/api/v1/discoveries/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDiscoverRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void toggleActive_withoutAdminRole_shouldReturnForbidden() throws Exception {
        mockMvc.perform(patch("/api/v1/discoveries/1/toggle-active"))
                .andExpect(status().isForbidden());
    }
} 