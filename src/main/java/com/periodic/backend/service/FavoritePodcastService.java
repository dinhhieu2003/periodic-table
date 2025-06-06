package com.periodic.backend.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.periodic.backend.domain.entity.FavoritePodcast;
import com.periodic.backend.domain.entity.Podcast;
import com.periodic.backend.domain.entity.User;
import com.periodic.backend.domain.response.favoritePodcast.CheckActiveFavoritePodcastResponse;
import com.periodic.backend.domain.response.favoritePodcast.FavoritePodcastResponse;
import com.periodic.backend.domain.response.favoritePodcast.FavoritePodcastShortResponse;
import com.periodic.backend.domain.response.favoritePodcast.ToggleActiveFavoritePodcastResponse;
import com.periodic.backend.domain.response.pagination.PaginationResponse;
import com.periodic.backend.mapper.FavoritePodcastMapper;
import com.periodic.backend.repository.FavoritePodcastRepository;
import com.periodic.backend.repository.specification.FavoritePodcastSpecification;
import com.periodic.backend.util.PaginationUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FavoritePodcastService {
    private final FavoritePodcastRepository favoritePodcastRepository;
    private final UserService userService;
    private final PodcastService podcastService;
    private final FavoritePodcastMapper favoritePodcastMapper;

    public ToggleActiveFavoritePodcastResponse toggleActive(Long podcastId) {
        log.info("Start: Function toggle active favorite podcast");
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.getUserByEmail(username);
        Podcast podcast = podcastService.getPodcastById(podcastId);
        var favoritePodcastExisted = favoritePodcastRepository.findByUser_IdAndPodcast_Id(user.getId(), podcastId);

        // if not in database - we gonna create it
        if (favoritePodcastExisted.isEmpty()) {
            FavoritePodcast newFavoritePodcast = new FavoritePodcast(user, podcast, Instant.now());
            newFavoritePodcast.setActive(true);
            favoritePodcastRepository.save(newFavoritePodcast);
            ToggleActiveFavoritePodcastResponse response = new ToggleActiveFavoritePodcastResponse(podcastId, true);
            log.info("End: Function toggle active success - create new favorite podcast");
            return response;
        }

        FavoritePodcast favoritePodcast = favoritePodcastExisted.get();
        boolean active = !favoritePodcast.isActive();
        favoritePodcast.setActive(active);
        FavoritePodcast updatedFavoritePodcast = favoritePodcastRepository.save(favoritePodcast);
        ToggleActiveFavoritePodcastResponse response = new ToggleActiveFavoritePodcastResponse(podcastId, updatedFavoritePodcast.isActive());
        log.info("End: Function toggle active success");
        return response;
    }
    
    public List<FavoritePodcastShortResponse> getFavoritePodcasts() {
    	var authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.getUserByEmail(username);
        String[] sortBy = {"lastSeen"};
        String term = user.getEmail();
        String[] sortDirection = null;
        Boolean active = true;
        FavoritePodcastSpecification spec = new FavoritePodcastSpecification(term, sortBy, sortDirection, active);
        List<FavoritePodcast> favoritePodcasts = favoritePodcastRepository.findAll(spec);
        List<FavoritePodcastShortResponse> response = favoritePodcastMapper.listFavoritePodcastToListFavoritePodcastShortResponse(favoritePodcasts);
        return response;
    }

    public PaginationResponse<List<FavoritePodcastShortResponse>> getFavoritePodcasts(Pageable pageable, String term, String[] sortBy, String[] sortDirection, Boolean active) {
        log.info("Start: Get favorite podcasts with search term: {}, sort by: {}, sort direction: {}, and active: {}", 
                term, sortBy != null ? String.join(",", sortBy) : null, 
                sortDirection != null ? String.join(",", sortDirection) : null, active);
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.getUserByEmail(username);
        if (sortBy == null) {
            sortBy = new String[] {"lastSeen"};
        }
        term = user.getEmail();
        if(active == null) {
        	active = true;
        }
        FavoritePodcastSpecification spec = new FavoritePodcastSpecification(term, sortBy, sortDirection, active);
        Page<FavoritePodcast> pageFavoritePodcast = favoritePodcastRepository.findAll(spec, pageable);
        Page<FavoritePodcastShortResponse> pageData = favoritePodcastMapper.pageFavoritePodcastToPageFavoritePodcastShortResponse(pageFavoritePodcast);
        PaginationResponse<List<FavoritePodcastShortResponse>> response = 
                PaginationUtils.buildPaginationResponse(pageable, pageData);
        log.info("End: Function get favorite podcasts success");
        return response;
    }
    
    public CheckActiveFavoritePodcastResponse checkActive(Long podcastId) {
    	var authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.getUserByEmail(username);
        Long userId = user.getId();
        Optional<FavoritePodcast> existFavoritePodcast = favoritePodcastRepository.findByUser_IdAndPodcast_Id(userId, podcastId);
        CheckActiveFavoritePodcastResponse response = new CheckActiveFavoritePodcastResponse(podcastId, false);
        if(existFavoritePodcast.isPresent()) {
        	response.setActive(existFavoritePodcast.get().isActive());
        }
        return response;
    }
} 