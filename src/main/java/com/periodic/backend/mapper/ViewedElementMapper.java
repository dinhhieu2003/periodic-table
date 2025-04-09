package com.periodic.backend.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;

import com.periodic.backend.domain.entity.ViewedElement;
import com.periodic.backend.domain.response.viewedElement.ViewedElementResponse;

@Component
@Mapper(componentModel = "spring")
public interface ViewedElementMapper {
	ViewedElementResponse viewedElementToViewedElementResponse(ViewedElement viewedElement);
	default Page<ViewedElementResponse> pageViewedElementToPageViewedElementResponse(Page<ViewedElement> pageViewedElement) {
		List<ViewedElementResponse> content = pageViewedElement.getContent()
				.stream()
				.map(this::viewedElementToViewedElementResponse)
				.collect(Collectors.toList());
		return new PageImpl<>(content, pageViewedElement.getPageable(), pageViewedElement.getTotalElements());
	}

}
