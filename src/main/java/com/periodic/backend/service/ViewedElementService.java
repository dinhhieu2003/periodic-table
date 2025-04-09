package com.periodic.backend.service;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.periodic.backend.domain.entity.Element;
import com.periodic.backend.domain.entity.User;
import com.periodic.backend.domain.entity.ViewedElement;
import com.periodic.backend.domain.response.pagination.PaginationResponse;
import com.periodic.backend.domain.response.viewedElement.ViewedElementResponse;
import com.periodic.backend.mapper.ViewedElementMapper;
import com.periodic.backend.repository.ViewedElementRepository;
import com.periodic.backend.repository.specification.ViewedElementSpecification;
import com.periodic.backend.util.PaginationUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ViewedElementService {
	private final ViewedElementRepository viewedElementRepository;
	private final ElementService elementService;
	private final UserService userService;
	private final ViewedElementMapper viewedElementMapper;
	
	public ViewedElementResponse create(Long elementId) {
		log.info("Start: function create/update viewed element");
		Element element = elementService.getElementById(elementId);
		var authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.getUserByEmail(username);
        ViewedElement viewedElement = new ViewedElement();
        viewedElement.setElement(element);
        viewedElement.setUser(user);
        viewedElement.setActive(true);
        viewedElement.setLastSeen(Instant.now());
        ViewedElement updatedViewedElement = viewedElementRepository.save(viewedElement);
        ViewedElementResponse viewedElementResponse = viewedElementMapper.viewedElementToViewedElementResponse(updatedViewedElement);
        log.info("End: function create/update viewed element success");
        return viewedElementResponse;
	}
	
	public PaginationResponse<List<ViewedElementResponse>> getViewedElements(Pageable pageable, String term, String[] sortBy, String[] sortDirection, Boolean active) {
		log.info("Start: Get viewed elements with search term: {}, sort by: {}, sort direction: {}, and active: {}", 
				term, sortBy != null ? String.join(",", sortBy) : null, 
				sortDirection != null ? String.join(",", sortDirection) : null, active);
		var authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.getUserByEmail(username);
        // set term regardless 
        term = user.getEmail();
        // set default sort by
        if(sortBy == null) {
        	sortBy = new String[]{"lastSeen"};
        }
        ViewedElementSpecification specification = new ViewedElementSpecification(term, sortBy, sortDirection, active);
        Page<ViewedElement> pageViewedElement = viewedElementRepository.findAll(specification, pageable);
        Page<ViewedElementResponse> pageData = viewedElementMapper.pageViewedElementToPageViewedElementResponse(pageViewedElement);
        PaginationResponse<List<ViewedElementResponse>> response = PaginationUtils.buildPaginationResponse(pageable, pageData);
        log.info("End: function get viewed elements of {}", user.getEmail());
        return response;
	}
}
