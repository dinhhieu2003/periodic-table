package com.periodic.backend.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.periodic.backend.domain.entity.Discover;
import com.periodic.backend.domain.entity.Element;
import com.periodic.backend.domain.entity.Scientist;
import com.periodic.backend.domain.request.discover.CreateDiscoverRequest;
import com.periodic.backend.domain.request.discover.UpdateDiscoverRequest;
import com.periodic.backend.domain.response.discover.CreateDiscoverResponse;
import com.periodic.backend.domain.response.discover.GetDiscoverResponse;
import com.periodic.backend.domain.response.discover.ToggleActiveDiscoverResponse;
import com.periodic.backend.domain.response.discover.UpdateDiscoverResponse;
import com.periodic.backend.domain.response.pagination.PaginationResponse;
import com.periodic.backend.exception.AppException;
import com.periodic.backend.mapper.DiscoverMapper;
import com.periodic.backend.repository.DiscoverRepository;
import com.periodic.backend.util.PaginationUtils;
import com.periodic.backend.util.constant.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DiscoverService {
    private final Logger log = LoggerFactory.getLogger(DiscoverService.class);
    private final DiscoverRepository discoverRepository;
    private final ScientistService scientistService;
    private final ElementService elementService;
    private final DiscoverMapper discoverMapper;
    
    public CreateDiscoverResponse createDiscover(CreateDiscoverRequest request) {
        log.info("Start: Create a new discovery");
        
        // Check if discovery already exists
        if (discoverRepository.existsByScientistIdAndElementId(request.getScientistId(), request.getElementId())) {
            log.error("Discovery already exists for scientist ID {} and element ID {}", 
                    request.getScientistId(), request.getElementId());
            throw new AppException(ErrorCode.DISCOVERY_ALREADY_EXISTS);
        }
        
        // Get scientist and element
        Scientist scientist = scientistService.getScientistById(request.getScientistId());
        Element element = elementService.getElementById(request.getElementId());
        
        // Create discovery
        Discover discover = discoverMapper.createDiscoverRequestToDiscover(request);
        discover.setScientist(scientist);
        discover.setElement(element);
        
        Discover savedDiscover = discoverRepository.save(discover);
        CreateDiscoverResponse response = discoverMapper.discoverToCreateDiscoverResponse(savedDiscover);
        
        log.info("End: Create a new discovery success with ID {}", response.getId());
        return response;
    }
    
    public PaginationResponse<List<GetDiscoverResponse>> getDiscoveries(Pageable pageable, String term, String searchBy) {
        log.info("Start: Function get discoveries pageable");
        Page<Discover> pageDiscover = null;
        
        if (term.isEmpty()) {
            log.info("Find all discoveries in database");
            pageDiscover = discoverRepository.findAll(pageable);
        } else if ("scientist".equals(searchBy)) {
            log.info("Find all discoveries in database with scientist name {}", term);
            pageDiscover = discoverRepository.findByScientistNameContainingIgnoreCase(pageable, term);
        } else if ("element".equals(searchBy)) {
            log.info("Find all discoveries in database with element name {}", term);
            pageDiscover = discoverRepository.findByElementNameContainingIgnoreCase(pageable, term);
        } else {
            log.info("Invalid searchBy parameter, finding all discoveries");
            pageDiscover = discoverRepository.findAll(pageable);
        }
        
        Page<GetDiscoverResponse> pageData = discoverMapper.pageDiscoverToPageGetDiscoverResponse(pageDiscover);
        PaginationResponse<List<GetDiscoverResponse>> response =
                PaginationUtils.buildPaginationResponse(pageable, pageData);
        
        log.info("End: Function get discoveries pageable success");
        return response;
    }
    
    public GetDiscoverResponse getDiscover(Long id) {
        log.info("Start: Get discovery by id {}", id);
        Discover discover = findDiscoverById(id);
        GetDiscoverResponse response = discoverMapper.discoverToGetDiscoverResponse(discover);
        log.info("End: Get discovery by id {} success", id);
        return response;
    }
    
    public UpdateDiscoverResponse updateDiscover(Long id, UpdateDiscoverRequest request) {
        log.info("Start: Function update discovery id {}", id);
        
        // Find existing discovery
        Discover existingDiscover = findDiscoverById(id);
        
        // Check if trying to update to a combination that already exists (but not for this ID)
        if (discoverRepository.existsByScientistIdAndElementId(request.getScientistId(), request.getElementId()) &&
           !(existingDiscover.getScientist().getId().equals(request.getScientistId()) && 
             existingDiscover.getElement().getId().equals(request.getElementId()))) {
            log.error("Discovery already exists for scientist ID {} and element ID {}", 
                    request.getScientistId(), request.getElementId());
            throw new AppException(ErrorCode.DISCOVERY_ALREADY_EXISTS);
        }
        
        // Get scientist and element
        Scientist scientist = scientistService.getScientistById(request.getScientistId());
        Element element = elementService.getElementById(request.getElementId());
        
        // Create updated discovery
        Discover requestDiscover = discoverMapper.updateDiscoverRequestToDiscover(request);
        requestDiscover.setId(existingDiscover.getId());
        requestDiscover.setScientist(scientist);
        requestDiscover.setElement(element);
        
        Discover updatedDiscover = discoverRepository.save(requestDiscover);
        UpdateDiscoverResponse response = discoverMapper.discoverToUpdateDiscoverResponse(updatedDiscover);
        
        log.info("End: Function update discovery id {} success", id);
        return response;
    }
    
    public ToggleActiveDiscoverResponse toggleActive(Long id) {
        log.info("Start: Function toggle active for discovery id {}", id);
        Discover discover = findDiscoverById(id);
        boolean active = !discover.isActive();
        discover.setActive(active);
        Discover updatedDiscover = discoverRepository.save(discover);
        log.info("Update active discovery into database success");
        ToggleActiveDiscoverResponse response = discoverMapper.discoverToToggleActiveDiscoverResponse(updatedDiscover);
        log.info("End: Function toggle active for discovery id {} success", id);
        return response;
    }
    
    private Discover findDiscoverById(Long id) {
        return discoverRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DISCOVERY_NOT_FOUND));
    }
} 