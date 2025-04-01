package com.periodic.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.periodic.backend.domain.entity.Podcast;

public interface PodcastRepository extends JpaRepository<Podcast, Long> {
	Page<Podcast> findByTitleContainingIgnoreCase(Pageable pageable, String title);
}
