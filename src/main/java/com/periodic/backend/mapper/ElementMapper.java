package com.periodic.backend.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;

import com.periodic.backend.domain.entity.Element;
import com.periodic.backend.domain.request.element.CreateElementRequest;
import com.periodic.backend.domain.request.element.UpdateElementRequest;
import com.periodic.backend.domain.response.element.CreateElementResponse;
import com.periodic.backend.domain.response.element.GetElementResponse;
import com.periodic.backend.domain.response.element.UpdateElementResponse;


@Component
@Mapper(componentModel = "spring")
public interface ElementMapper {
	ElementMapper INSTANCE = Mappers.getMapper(ElementMapper.class);
	
	CreateElementResponse elementToCreateElementResponse(Element element);
	Element createElementRequestToElement(CreateElementRequest createElementRequest);
	GetElementResponse elementToGetElementResponse(Element element);
	
	default Page<GetElementResponse> pageElementToPageGetElementResponse(Page<Element> pageElement) {
		List<GetElementResponse> content = pageElement.getContent()
				.stream()
				.map(this::elementToGetElementResponse)
				.collect(Collectors.toList());
		return new PageImpl<>(content, pageElement.getPageable(), pageElement.getTotalElements());
	}
	
	Element updateElementRequestToElement(UpdateElementRequest updateElementRequest); 
	UpdateElementResponse elementToUpdateElementResponse(Element element);
}
