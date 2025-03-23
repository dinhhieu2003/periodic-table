package com.periodic.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.periodic.backend.domain.entity.Scientist;
import com.periodic.backend.domain.request.scientist.CreateScientistRequest;
import com.periodic.backend.domain.response.scientist.CreateScientistResponse;
import com.periodic.backend.mapper.ScientistMapper;
import com.periodic.backend.repository.ScientistRepository;

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
}
