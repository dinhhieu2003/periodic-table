package com.periodic.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.periodic.backend.domain.entity.ViewedPodcast;

@Repository
public interface ViewedPodcastRepository extends JpaRepository<ViewedPodcast, Long>, JpaSpecificationExecutor<ViewedPodcast> {
    Optional<ViewedPodcast> findByUser_IdAndPodcast_Id(Long userId, Long podcastId);
} 