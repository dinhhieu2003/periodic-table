package com.periodic.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.periodic.backend.domain.entity.Discover;

public interface DiscoverRepository extends JpaRepository<Discover, Long>, JpaSpecificationExecutor<Discover> {
    Page<Discover> findByScientistNameContainingIgnoreCase(Pageable pageable, String name);
    Page<Discover> findByElementNameContainingIgnoreCase(Pageable pageable, String name);
    boolean existsByScientistIdAndElementId(Long scientistId, Long elementId);
} 