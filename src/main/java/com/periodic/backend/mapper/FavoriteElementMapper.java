package com.periodic.backend.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;

import com.periodic.backend.domain.entity.FavoriteElement;
import com.periodic.backend.domain.response.favoriteElement.FavoriteElementResponse;

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
}
