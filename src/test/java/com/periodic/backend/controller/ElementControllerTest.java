package com.periodic.backend.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
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
import com.periodic.backend.domain.request.element.CreateElementRequest;
import com.periodic.backend.domain.response.element.CreateElementResponse;
import com.periodic.backend.domain.response.element.GetElementResponse;
import com.periodic.backend.domain.response.pagination.Meta;
import com.periodic.backend.domain.response.pagination.PaginationResponse;
import com.periodic.backend.service.ElementService;
import com.periodic.backend.util.constant.StandardState;

@WebMvcTest(ElementController.class)
public class ElementControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	
	@MockBean
	private ElementService elementService;
	
	private CreateElementRequest createElementRequest;
    private CreateElementResponse createElementResponse;
    private PaginationResponse<List<GetElementResponse>> paginationResponse;
    private GetElementResponse getElementResponse;
    
    @BeforeEach
    void setup() {
    	createElementRequest = CreateElementRequest.builder()
    		    .symbol("H")
    		    .name("Hydrogen")
    		    .atomicNumber("1")
    		    .groupNumber("1")
    		    .period("1")
    		    .block("s")
    		    .classification("phi kim")
    		    .meltingPoint(14.0)
    		    .boilingPoint(20.0)
    		    .atomicMass("1.00794(4)")
    		    .electronicConfiguration("1s1")
    		    .electronegativity(2.2)
    		    .atomicRadius(37.0)
    		    .ionRadius("0.0")
    		    .vanDelWaalsRadius(120.0)
    		    .ionizationEnergy(1312.0)
    		    .electronAffinity(-73.0)
    		    .oxidationStates(Arrays.asList(-1, 1))
    		    .standardState(StandardState.GAS)
    		    .bondingType("diatomic")
    		    .density(8.99e-05)
    		    .yearDiscovered(1766)
    		    .build();
    	
    	createElementResponse = CreateElementResponse.builder()
    			.id(1L)
    			.symbol("H")
    		    .name("Hydrogen")
    		    .atomicNumber("1")
    		    .groupNumber("1")
    		    .period("1")
    		    .block("s")
    		    .classification("phi kim")
    		    .meltingPoint(14.0)
    		    .boilingPoint(20.0)
    		    .atomicMass("1.00794(4)")
    		    .electronicConfiguration("1s1")
    		    .electronegativity(2.2)
    		    .atomicRadius(37.0)
    		    .ionRadius("0.0")
    		    .vanDelWaalsRadius(120.0)
    		    .ionizationEnergy(1312.0)
    		    .electronAffinity(-73.0)
    		    .oxidationStates(Arrays.asList(-1, 1))
    		    .standardState(StandardState.GAS)
    		    .bondingType("diatomic")
    		    .density(8.99e-05)
    		    .yearDiscovered(1766)
    		    .build();
    	
    	List<GetElementResponse> elements = new ArrayList<>();
        GetElementResponse element = GetElementResponse.builder()
        		.id(1L)
    			.symbol("H")
    		    .name("Hydrogen")
    		    .atomicNumber("1")
    		    .groupNumber("1")
    		    .period("1")
    		    .block("s")
    		    .classification("phi kim")
    		    .meltingPoint(14.0)
    		    .boilingPoint(20.0)
    		    .atomicMass("1.00794(4)")
    		    .electronicConfiguration("1s1")
    		    .electronegativity(2.2)
    		    .atomicRadius(37.0)
    		    .ionRadius("0.0")
    		    .vanDelWaalsRadius(120.0)
    		    .ionizationEnergy(1312.0)
    		    .electronAffinity(-73.0)
    		    .oxidationStates(Arrays.asList(-1, 1))
    		    .standardState(StandardState.GAS)
    		    .bondingType("diatomic")
    		    .density(8.99e-05)
    		    .yearDiscovered(1766)
    		    .build();
        elements.add(element);
        
        Meta meta = Meta.builder()
        		.current(1)
        		.pageSize(10)
        		.totalItems(1)
        		.totalPages(1).build();
        paginationResponse = new PaginationResponse<>();
        paginationResponse.setMeta(meta);
        paginationResponse.setResult(elements);
        
        getElementResponse = GetElementResponse.builder()
        		.id(1L)
    			.symbol("H")
    		    .name("Hydrogen")
    		    .atomicNumber("1")
    		    .groupNumber("1")
    		    .period("1")
    		    .block("s")
    		    .classification("phi kim")
    		    .meltingPoint(14.0)
    		    .boilingPoint(20.0)
    		    .atomicMass("1.00794(4)")
    		    .electronicConfiguration("1s1")
    		    .electronegativity(2.2)
    		    .atomicRadius(37.0)
    		    .ionRadius("0.0")
    		    .vanDelWaalsRadius(120.0)
    		    .ionizationEnergy(1312.0)
    		    .electronAffinity(-73.0)
    		    .oxidationStates(Arrays.asList(-1, 1))
    		    .standardState(StandardState.GAS)
    		    .bondingType("diatomic")
    		    .density(8.99e-05)
    		    .yearDiscovered(1766)
    		    .build();
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void createElement_withValidRequest_shouldReturnCreatedElement() throws Exception {
        when(elementService.createElement(any(CreateElementRequest.class))).thenReturn(createElementResponse);

        mockMvc.perform(post("/api/v1/elements").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createElementRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.name").value("Hydrogen"))
                .andExpect(jsonPath("$.data.symbol").value("H"));
    }
    
    @Test
    @WithMockUser(roles = "USER")
    void createElement_withoutAdminRole_shouldReturnForbidden() throws Exception {
        mockMvc.perform(post("/api/v1/elements")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createElementRequest)))
                .andExpect(status().isForbidden());
    }
    
    @Test
    @WithMockUser
    void getElements_shouldReturnPagedElements() throws Exception {
        when(elementService.getElements(any(Pageable.class), anyString())).thenReturn(paginationResponse);

        mockMvc.perform(get("/api/v1/elements")
                .param("current", "1")
                .param("pageSize", "10"))
        
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.result[0].name").value("Hydrogen"))
                .andExpect(jsonPath("$.data.meta.current").value(1))
                .andExpect(jsonPath("$.data.meta.totalPages").value(1))
                .andExpect(jsonPath("$.data.meta.totalItems").value(1));
    }
    
    @Test
    @WithMockUser
    void getElements_withSearchTerm_shouldReturnFilteredElements() throws Exception {
        when(elementService.getElements(any(Pageable.class), anyString())).thenReturn(paginationResponse);

        mockMvc.perform(get("/api/v1/elements")
                .param("current", "1")
                .param("pageSize", "10")
                .param("term", "Hyd"))
        
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.result[0].name").value("Hydrogen"));
    }
    
    @Test
    @WithMockUser
    void getElement_shouldReturnElement() throws Exception {
    	when(elementService.getElement(1L)).thenReturn(getElementResponse);
    	
    	mockMvc.perform(get("/api/v1/elements/{id}", 1L)
    		.contentType(MediaType.APPLICATION_JSON))
    	
    		.andExpect(status().isOk())
            .andExpect(jsonPath("data.id").value(1))
            .andExpect(jsonPath("data.name").value("Hydrogen"))
            .andExpect(jsonPath("data.symbol").value("H"));
    }
}
