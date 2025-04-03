package com.periodic.backend.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;

import com.periodic.backend.domain.entity.Milestone;
import com.periodic.backend.domain.request.milestone.CreateMilestoneRequest;
import com.periodic.backend.domain.request.milestone.UpdateMilestoneRequest;
import com.periodic.backend.domain.response.milestone.CreateMilestoneResponse;
import com.periodic.backend.domain.response.milestone.GetMilestoneResponse;
import com.periodic.backend.domain.response.milestone.ToggleActiveMilestoneResponse;
import com.periodic.backend.domain.response.milestone.UpdateMilestoneResponse;

@Component
@Mapper(componentModel = "spring")
public interface MilestoneMapper {
    MilestoneMapper INSTANCE = Mappers.getMapper(MilestoneMapper.class);
    
    @Mapping(target = "scientist", ignore = true)
    Milestone createMilestoneRequestToMilestone(CreateMilestoneRequest createMilestoneRequest);
    
    @Mapping(target = "scientist", ignore = true)
    Milestone updateMilestoneRequestToMilestone(UpdateMilestoneRequest updateMilestoneRequest);
    
    @Mapping(source = "scientist.name", target = "scientistName")
    CreateMilestoneResponse milestoneToCreateMilestoneResponse(Milestone milestone);
    
    @Mapping(source = "scientist.name", target = "scientistName")
    GetMilestoneResponse milestoneToGetMilestoneResponse(Milestone milestone);
    
    @Mapping(source = "scientist.name", target = "scientistName")
    UpdateMilestoneResponse milestoneToUpdateMilestoneResponse(Milestone milestone);
    
    default Page<GetMilestoneResponse> pageMilestoneToPageGetMilestoneResponse(Page<Milestone> pageMilestone) {
        List<GetMilestoneResponse> content = pageMilestone.getContent()
                .stream()
                .map(this::milestoneToGetMilestoneResponse)
                .collect(Collectors.toList());
        return new PageImpl<>(content, pageMilestone.getPageable(), pageMilestone.getTotalElements());
    }
    
    default ToggleActiveMilestoneResponse milestoneToToggleActiveMilestoneResponse(Milestone milestone) {
        return new ToggleActiveMilestoneResponse(milestone.getId(), milestone.isActive());
    }
} 