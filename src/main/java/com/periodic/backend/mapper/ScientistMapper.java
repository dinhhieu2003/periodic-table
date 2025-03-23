package com.periodic.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

import com.periodic.backend.domain.entity.Scientist;
import com.periodic.backend.domain.request.scientist.CreateScientistRequest;
import com.periodic.backend.domain.response.scientist.CreateScientistResponse;

@Component
@Mapper(componentModel = "spring")
public interface ScientistMapper {
	ScientistMapper INSTANCE = Mappers.getMapper(ScientistMapper.class);
	
	Scientist createScientistRequestToScientist(CreateScientistRequest createScientistRequest);
	CreateScientistResponse scientistToCreateScientistResponse(Scientist scientist);
}
