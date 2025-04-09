package com.periodic.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.periodic.backend.domain.entity.FavoritePodcast;

@Repository
public interface FavoritePodcastRepository extends JpaRepository<FavoritePodcast, Long>, JpaSpecificationExecutor<FavoritePodcast> {
    Optional<FavoritePodcast> findByUser_IdAndPodcast_Id(Long userId, Long podcastId);
} 