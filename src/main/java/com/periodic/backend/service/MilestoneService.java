package com.periodic.backend.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.periodic.backend.domain.entity.Milestone;
import com.periodic.backend.domain.entity.Scientist;
import com.periodic.backend.domain.request.milestone.CreateMilestoneRequest;
import com.periodic.backend.domain.request.milestone.UpdateMilestoneRequest;
import com.periodic.backend.domain.response.milestone.CreateMilestoneResponse;
import com.periodic.backend.domain.response.milestone.GetMilestoneResponse;
import com.periodic.backend.domain.response.milestone.ToggleActiveMilestoneResponse;
import com.periodic.backend.domain.response.milestone.UpdateMilestoneResponse;
import com.periodic.backend.domain.response.pagination.PaginationResponse;
import com.periodic.backend.exception.AppException;
import com.periodic.backend.mapper.MilestoneMapper;
import com.periodic.backend.repository.MilestoneRepository;
import com.periodic.backend.util.PaginationUtils;
import com.periodic.backend.util.constant.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MilestoneService {
    private final Logger log = LoggerFactory.getLogger(MilestoneService.class);
    private final MilestoneRepository milestoneRepository;
    private final ScientistService scientistService;
    private final MilestoneMapper milestoneMapper;
    
    public CreateMilestoneResponse createMilestone(CreateMilestoneRequest request) {
        log.info("Start: Create a new milestone");
        
        // Get scientist
        Scientist scientist = scientistService.getScientistById(request.getScientistId());
        
        // Create milestone
        Milestone milestone = milestoneMapper.createMilestoneRequestToMilestone(request);
        milestone.setScientist(scientist);
        
        Milestone savedMilestone = milestoneRepository.save(milestone);
        CreateMilestoneResponse response = milestoneMapper.milestoneToCreateMilestoneResponse(savedMilestone);
        
        log.info("End: Create a new milestone success with ID {}", response.getId());
        return response;
    }
    
    public PaginationResponse<List<GetMilestoneResponse>> getMilestones(Pageable pageable, String term, String searchBy) {
        log.info("Start: Function get milestones pageable");
        Page<Milestone> pageMilestone = null;
        
        if (term.isEmpty()) {
            log.info("Find all milestones in database");
            pageMilestone = milestoneRepository.findAll(pageable);
        } else if ("scientist".equals(searchBy)) {
            log.info("Find all milestones in database with scientist name {}", term);
            pageMilestone = milestoneRepository.findByScientistNameContainingIgnoreCase(pageable, term);
        } else if ("milestone".equals(searchBy)) {
            log.info("Find all milestones in database with milestone description containing {}", term);
            pageMilestone = milestoneRepository.findByMilestoneContainingIgnoreCase(pageable, term);
        } else if ("year".equals(searchBy)) {
            try {
                int year = Integer.parseInt(term);
                log.info("Find all milestones in database for year {}", year);
                pageMilestone = milestoneRepository.findByYear(pageable, year);
            } catch (NumberFormatException e) {
                log.info("Invalid year format, finding all milestones");
                pageMilestone = milestoneRepository.findAll(pageable);
            }
        } else {
            log.info("Invalid searchBy parameter, finding all milestones");
            pageMilestone = milestoneRepository.findAll(pageable);
        }
        
        Page<GetMilestoneResponse> pageData = milestoneMapper.pageMilestoneToPageGetMilestoneResponse(pageMilestone);
        PaginationResponse<List<GetMilestoneResponse>> response =
                PaginationUtils.buildPaginationResponse(pageable, pageData);
        
        log.info("End: Function get milestones pageable success");
        return response;
    }
    
    public GetMilestoneResponse getMilestone(Long id) {
        log.info("Start: Get milestone by id {}", id);
        Milestone milestone = findMilestoneById(id);
        GetMilestoneResponse response = milestoneMapper.milestoneToGetMilestoneResponse(milestone);
        log.info("End: Get milestone by id {} success", id);
        return response;
    }
    
    public UpdateMilestoneResponse updateMilestone(Long id, UpdateMilestoneRequest request) {
        log.info("Start: Function update milestone id {}", id);
        
        // Find existing milestone
        Milestone existingMilestone = findMilestoneById(id);
        
        // Get scientist
        Scientist scientist = scientistService.getScientistById(request.getScientistId());
        
        // Create updated milestone
        Milestone requestMilestone = milestoneMapper.updateMilestoneRequestToMilestone(request);
        requestMilestone.setId(existingMilestone.getId());
        requestMilestone.setScientist(scientist);
        
        Milestone updatedMilestone = milestoneRepository.save(requestMilestone);
        UpdateMilestoneResponse response = milestoneMapper.milestoneToUpdateMilestoneResponse(updatedMilestone);
        
        log.info("End: Function update milestone id {} success", id);
        return response;
    }
    
    public ToggleActiveMilestoneResponse toggleActive(Long id) {
        log.info("Start: Function toggle active for milestone id {}", id);
        Milestone milestone = findMilestoneById(id);
        boolean active = !milestone.isActive();
        milestone.setActive(active);
        Milestone updatedMilestone = milestoneRepository.save(milestone);
        log.info("Update active milestone into database success");
        ToggleActiveMilestoneResponse response = milestoneMapper.milestoneToToggleActiveMilestoneResponse(updatedMilestone);
        log.info("End: Function toggle active for milestone id {} success", id);
        return response;
    }
    
    private Milestone findMilestoneById(Long id) {
        return milestoneRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.MILESTONE_NOT_FOUND));
    }
} 