package com.periodic.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.periodic.backend.domain.entity.Milestone;

public interface MilestoneRepository extends JpaRepository<Milestone, Long> {
    Page<Milestone> findByScientistNameContainingIgnoreCase(Pageable pageable, String name);
    Page<Milestone> findByMilestoneContainingIgnoreCase(Pageable pageable, String term);
    Page<Milestone> findByYear(Pageable pageable, int year);
}