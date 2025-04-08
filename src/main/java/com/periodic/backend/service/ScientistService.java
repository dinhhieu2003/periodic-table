package com.periodic.backend.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.periodic.backend.domain.entity.Scientist;
import com.periodic.backend.domain.request.scientist.CreateScientistRequest;
import com.periodic.backend.domain.request.scientist.UpdateScientistRequest;
import com.periodic.backend.domain.response.pagination.PaginationResponse;
import com.periodic.backend.domain.response.scientist.CreateScientistResponse;
import com.periodic.backend.domain.response.scientist.GetScientistResponse;
import com.periodic.backend.domain.response.scientist.ToggleActiveScientistResponse;
import com.periodic.backend.domain.response.scientist.UpdateScientistResponse;
import com.periodic.backend.exception.AppException;
import com.periodic.backend.mapper.ScientistMapper;
import com.periodic.backend.repository.ScientistRepository;
import com.periodic.backend.repository.specification.ScientistSpecification;
import com.periodic.backend.util.PaginationUtils;
import com.periodic.backend.util.constant.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScientistService {
	private final Logger log = LoggerFactory.getLogger(ScientistService.class);
	private final ScientistRepository scientistRepository;
	private final ScientistMapper scientistMapper;
	
	public CreateScientistResponse createScientist(CreateScientistRequest createScientistRequest) {
		log.info("Start: Create a new scientist");
		Scientist scientist = scientistMapper.createScientistRequestToScientist(createScientistRequest);
		Scientist savedScientist = scientistRepository.save(scientist);
		CreateScientistResponse createScientistResponse = scientistMapper.scientistToCreateScientistResponse(savedScientist);
		log.info("End: Create a new scientist success");
		return createScientistResponse;
	}
	
	public PaginationResponse<List<GetScientistResponse>> getScientists(Pageable pageable, String term, String[] sortBy, String[] sortDirection, Boolean active) {
		log.info("Start: Get scientists with search term: {}, sort by: {}, sort direction: {}, and active: {}", 
				term, sortBy != null ? String.join(",", sortBy) : null, 
				sortDirection != null ? String.join(",", sortDirection) : null, active);
		
		ScientistSpecification specification = new ScientistSpecification(term, sortBy, sortDirection, active);
		Page<Scientist> pageScientist = scientistRepository.findAll(specification, pageable);
		
		Page<GetScientistResponse> pageData = scientistMapper.pageScientistToPageGetScientistResponse(pageScientist);
		PaginationResponse<List<GetScientistResponse>> response = 
				PaginationUtils.buildPaginationResponse(pageable, pageData);
		
		log.info("End: Get scientists success");
		return response;
	}
	
	public GetScientistResponse getScientist(Long id) {
		Scientist scientist = scientistRepository.findById(id)
				.orElseThrow(() -> new AppException(ErrorCode.SCIENTIST_NOT_FOUND));
		GetScientistResponse response = scientistMapper.scientistToGetScientistResponse(scientist);
		return response;
	}
	
	public UpdateScientistResponse updateScientist(Long id, UpdateScientistRequest request) {
		log.info("Start: Function update scientist id {}", id);
		Scientist existScientist = scientistRepository.findById(id)
				.orElseThrow(() -> new AppException(ErrorCode.SCIENTIST_NOT_FOUND));
		Scientist requestScientist = scientistMapper.updateScientistRequestToScientist(request);
		requestScientist.setId(existScientist.getId());
		Scientist updatedScientis = scientistRepository.save(requestScientist);
		log.info("Update scientist id {} into database", id);
		UpdateScientistResponse response = scientistMapper.scientistToUpdateScientistResponse(updatedScientis);
		return response;
	}
	
	public ToggleActiveScientistResponse toggleActive(Long id) {
		log.info("Start: Function toggle active for scientist");
		Scientist scientist = scientistRepository.findById(id)
				.orElseThrow(() -> new AppException(ErrorCode.SCIENTIST_NOT_FOUND));
		boolean active = !scientist.isActive();
		scientist.setActive(active);
		Scientist updatedScientist = scientistRepository.save(scientist);
		log.info("Update active scientist into database success");
		ToggleActiveScientistResponse response = scientistMapper.scientistToToggleActiveResponse(updatedScientist);
		return response;
	}
	
	public Scientist getScientistById(Long id) {
		return scientistRepository.findById(id)
				.orElseThrow(() -> new AppException(ErrorCode.SCIENTIST_NOT_FOUND));
	}
} 
