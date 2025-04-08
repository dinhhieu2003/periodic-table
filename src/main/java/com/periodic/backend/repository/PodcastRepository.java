package com.periodic.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.periodic.backend.domain.entity.Podcast;

public interface PodcastRepository extends JpaRepository<Podcast, Long>, JpaSpecificationExecutor<Podcast> {
	Page<Podcast> findByTitleContainingIgnoreCase(Pageable pageable, String title);
	Page<Podcast> findByElementId(Pageable pageable, Long elementId);
}
