package com.periodic.backend.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;

import com.periodic.backend.domain.entity.Element;
import com.periodic.backend.domain.entity.ViewedElement;
import com.periodic.backend.domain.response.viewedElement.ViewedElementResponse;
import com.periodic.backend.domain.response.viewedElement.ViewedElementShortResponse;

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
	
	default Page<ViewedElementShortResponse> pageViewedElementToPageViewedElementShortResponse(Page<ViewedElement> pageViewedElement) {
		List<ViewedElementShortResponse> content = listViewedElementToListViewedElementShortRespsonse(pageViewedElement.getContent());
		return new PageImpl<>(content, pageViewedElement.getPageable(), pageViewedElement.getTotalElements());
	}
	
	default List<ViewedElementShortResponse> listViewedElementToListViewedElementShortRespsonse(List<ViewedElement> viewedELements) {
		List<ViewedElementShortResponse> response =
				viewedELements.stream()
				.map(vw -> {
					Element element = vw.getElement();
					return new ViewedElementShortResponse(
							element.getName(), element.getSymbol(),
							element.getImage(), vw.getLastSeen());
				})
				.collect(Collectors.toList());
		return response;
	}
}
