package com.periodic.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.periodic.backend.domain.entity.CommentPodcast;

public interface CommentPodcastRepository extends JpaRepository<CommentPodcast, Long> {
    Page<CommentPodcast> findByPodcastId(Pageable pageable, Long podcastId);
    Page<CommentPodcast> findByUserId(Pageable pageable, Long userId);
    Page<CommentPodcast> findByContentContainingIgnoreCase(Pageable pageable, String content);
} 