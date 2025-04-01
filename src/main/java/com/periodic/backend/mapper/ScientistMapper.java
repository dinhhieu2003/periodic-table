package com.periodic.backend.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;

import com.periodic.backend.domain.entity.Scientist;
import com.periodic.backend.domain.request.scientist.CreateScientistRequest;
import com.periodic.backend.domain.request.scientist.UpdateScientistRequest;
import com.periodic.backend.domain.response.scientist.CreateScientistResponse;
import com.periodic.backend.domain.response.scientist.GetScientistResponse;
import com.periodic.backend.domain.response.scientist.ToggleActiveScientistResponse;
import com.periodic.backend.domain.response.scientist.UpdateScientistResponse;

@Component
@Mapper(componentModel = "spring")
public interface ScientistMapper {
	ScientistMapper INSTANCE = Mappers.getMapper(ScientistMapper.class);
	
	Scientist createScientistRequestToScientist(CreateScientistRequest createScientistRequest);
	Scientist updateScientistRequestToScientist(UpdateScientistRequest updateScientistRequest);
	UpdateScientistResponse scientistToUpdateScientistResponse(Scientist scientist);
	CreateScientistResponse scientistToCreateScientistResponse(Scientist scientist);
	GetScientistResponse scientistToGetScientistResponse(Scientist scientist);
	
	default Page<GetScientistResponse> pageScientistToPageGetScientistResponse(Page<Scientist> pageScientist) {
		List<GetScientistResponse> content = pageScientist.getContent()
				.stream()
				.map(this::scientistToGetScientistResponse)
				.collect(Collectors.toList());
		return new PageImpl<>(content, pageScientist.getPageable(), pageScientist.getTotalElements());
	}
	
	default ToggleActiveScientistResponse scientistToToggleActiveResponse(Scientist scientist) {
		return new ToggleActiveScientistResponse(scientist.getId(), scientist.isActive());
	}
}
