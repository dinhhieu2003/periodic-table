package com.periodic.backend.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;

import com.periodic.backend.domain.entity.Element;
import com.periodic.backend.domain.entity.FavoriteElement;
import com.periodic.backend.domain.response.favoriteElement.FavoriteElementResponse;
import com.periodic.backend.domain.response.favoriteElement.FavoriteElementShortResponse;

@Component
@Mapper(componentModel = "spring")
public interface FavoriteElementMapper {
	FavoriteElementResponse favoriteElementToFavoriteElementResponse(FavoriteElement favoriteElement);
	default Page<FavoriteElementResponse> pageFavoriteElementToPageFavoriteElementResponse(Page<FavoriteElement> pageFavoriteElement) {
		List<FavoriteElementResponse> content = pageFavoriteElement.getContent()
				.stream()
				.map(this::favoriteElementToFavoriteElementResponse)
				.collect(Collectors.toList());
		return new PageImpl<>(content, pageFavoriteElement.getPageable(), pageFavoriteElement.getTotalElements());
	}
	
	default Page<FavoriteElementShortResponse> pageFavoriteElementToPageFavoriteElementShortResponse(Page<FavoriteElement> pageFavoriteElement) {
		List<FavoriteElementShortResponse> content = 
				listFavoriteElementToListFavoriteElementShortResponse(pageFavoriteElement.getContent());
		return new PageImpl<>(content, pageFavoriteElement.getPageable(), pageFavoriteElement.getTotalElements());
	}
	
	default List<FavoriteElementShortResponse> listFavoriteElementToListFavoriteElementShortResponse(List<FavoriteElement> favoriteElements) {
		List<FavoriteElementShortResponse> response = 
				favoriteElements.stream()
				.map(fav -> {
        			Element element = fav.getElement();
        			return new FavoriteElementShortResponse(
        					element.getName(),
        					element.getSymbol(),
        					element.getImage(), element.getId());
        		})
        		.collect(Collectors.toList());
		return response;
	}
}
