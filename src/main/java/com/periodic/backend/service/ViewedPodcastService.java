package com.periodic.backend.service;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.periodic.backend.domain.entity.Podcast;
import com.periodic.backend.domain.entity.User;
import com.periodic.backend.domain.entity.ViewedPodcast;
import com.periodic.backend.domain.response.pagination.PaginationResponse;
import com.periodic.backend.domain.response.viewedPodcast.ViewedPodcastResponse;
import com.periodic.backend.mapper.ViewedPodcastMapper;
import com.periodic.backend.repository.ViewedPodcastRepository;
import com.periodic.backend.repository.specification.ViewedPodcastSpecification;
import com.periodic.backend.util.PaginationUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ViewedPodcastService {
    private final ViewedPodcastRepository viewedPodcastRepository;
    private final PodcastService podcastService;
    private final UserService userService;
    private final ViewedPodcastMapper viewedPodcastMapper;

    public ViewedPodcastResponse create(Long podcastId) {
        log.info("Start: function create/update viewed podcast");
        Podcast podcast = podcastService.getPodcastById(podcastId);
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.getUserByEmail(username);
        ViewedPodcast viewedPodcast = new ViewedPodcast();
        viewedPodcast.setPodcast(podcast);
        viewedPodcast.setUser(user);
        viewedPodcast.setActive(true);
        viewedPodcast.setLastSeen(Instant.now());
        ViewedPodcast updatedViewedPodcast = viewedPodcastRepository.save(viewedPodcast);
        ViewedPodcastResponse viewedPodcastResponse = viewedPodcastMapper.viewedPodcastToViewedPodcastResponse(updatedViewedPodcast);
        log.info("End: function create/update viewed podcast success");
        return viewedPodcastResponse;
    }

    public PaginationResponse<List<ViewedPodcastResponse>> getViewedPodcasts(Pageable pageable, String term, String[] sortBy, String[] sortDirection, Boolean active) {
        log.info("Start: Get viewed podcasts with search term: {}, sort by: {}, sort direction: {}, and active: {}", 
                term, sortBy != null ? String.join(",", sortBy) : null, 
                sortDirection != null ? String.join(",", sortDirection) : null, active);
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.getUserByEmail(username);
        // set term regardless 
        term = user.getEmail();
        // set default sort by
        if (sortBy == null) {
            sortBy = new String[]{"lastSeen"};
        }
        ViewedPodcastSpecification specification = new ViewedPodcastSpecification(term, sortBy, sortDirection, active);
        Page<ViewedPodcast> pageViewedPodcast = viewedPodcastRepository.findAll(specification, pageable);
        Page<ViewedPodcastResponse> pageData = viewedPodcastMapper.pageViewedPodcastToPageViewedPodcastResponse(pageViewedPodcast);
        PaginationResponse<List<ViewedPodcastResponse>> response = PaginationUtils.buildPaginationResponse(pageable, pageData);
        log.info("End: function get viewed podcasts of {}", user.getEmail());
        return response;
    }
} 