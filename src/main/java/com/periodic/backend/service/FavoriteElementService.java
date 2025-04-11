package com.periodic.backend.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.periodic.backend.domain.entity.Element;
import com.periodic.backend.domain.entity.FavoriteElement;
import com.periodic.backend.domain.entity.User;
import com.periodic.backend.domain.response.favoriteElement.CheckActiveFavoriteElementResponse;
import com.periodic.backend.domain.response.favoriteElement.FavoriteElementResponse;
import com.periodic.backend.domain.response.favoriteElement.ToggleActiveFavoriteElementResponse;
import com.periodic.backend.domain.response.pagination.PaginationResponse;
import com.periodic.backend.mapper.FavoriteElementMapper;
import com.periodic.backend.repository.FavoriteElementRepository;
import com.periodic.backend.repository.specification.FavoriteElementSpecification;
import com.periodic.backend.util.PaginationUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FavoriteElementService {
	private final FavoriteElementRepository favoriteElementRepository;
	private final UserService userService;
	private final ElementService elementService;
	private final FavoriteElementMapper favoriteElementMapper;
	
	public ToggleActiveFavoriteElementResponse toggleActive(Long elementId) {
		log.info("Start: Function toggle active favorite element");
		var authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.getUserByEmail(username);
        Element element = elementService.getElementById(elementId);
		Optional<FavoriteElement> favoriteElementExisted = favoriteElementRepository.findByUser_IdAndElement_Id(user.getId(), elementId);
	
		// if not in database - we gonna create it
		if(favoriteElementExisted.isEmpty()) {
			FavoriteElement newFavoriteElement = new FavoriteElement(user, element, Instant.now());
			newFavoriteElement.setActive(true);
			favoriteElementRepository.save(newFavoriteElement);
			ToggleActiveFavoriteElementResponse response = new ToggleActiveFavoriteElementResponse(elementId, true);
			log.info("End: Function toggle active success - create new favorite element");
			return response;
		}
		FavoriteElement favoriteElement = favoriteElementExisted.get();
		boolean active = !favoriteElement.isActive();
		favoriteElement.setActive(active);
		favoriteElement.setLastSeen(Instant.now());
		FavoriteElement updatedFavoriteElement = favoriteElementRepository.save(favoriteElement);
		ToggleActiveFavoriteElementResponse response = new ToggleActiveFavoriteElementResponse(elementId, updatedFavoriteElement.isActive());
		log.info("End: Function toggle active success");
		return response;
	}
	
	public PaginationResponse<List<FavoriteElementResponse>> getFavoriteElements(Pageable pageable, String term, String[] sortBy, String[] sortDirection, Boolean active) {
		log.info("Start: Get favorite elements with search term: {}, sort by: {}, sort direction: {}, and active: {}", 
				term, sortBy != null ? String.join(",", sortBy) : null, 
				sortDirection != null ? String.join(",", sortDirection) : null, active);
		var authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.getUserByEmail(username);
		if(sortBy == null) {
			sortBy = new String[] {"lastSeen"};
		}
		term = user.getEmail();
		FavoriteElementSpecification spec = new FavoriteElementSpecification(term, sortBy, sortDirection, active);
		Page<FavoriteElement> pageFavoriteElement = favoriteElementRepository.findAll(spec, pageable);
		Page<FavoriteElementResponse> pageData = favoriteElementMapper.pageFavoriteElementToPageFavoriteElementResponse(pageFavoriteElement);
		PaginationResponse<List<FavoriteElementResponse>> response = 
				PaginationUtils.buildPaginationResponse(pageable, pageData);
		log.info("End: Function get favorite elements success");
		return response;
	}
	
	public CheckActiveFavoriteElementResponse checkActive(Long elementId) {
		var authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.getUserByEmail(username);
        Long userId = user.getId();
		Optional<FavoriteElement> existFavoriteElement = favoriteElementRepository.findByUser_IdAndElement_Id(userId, elementId);
		CheckActiveFavoriteElementResponse response = new CheckActiveFavoriteElementResponse(elementId, false);
		if(existFavoriteElement.isPresent()) {
			response.setActive(existFavoriteElement.get().isActive());
		}
		
		return response;
	}
}
