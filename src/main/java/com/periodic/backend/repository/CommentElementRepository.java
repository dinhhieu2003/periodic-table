package com.periodic.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.periodic.backend.domain.entity.CommentElement;

public interface CommentElementRepository extends JpaRepository<CommentElement, Long> {
    Page<CommentElement> findByElementId(Pageable pageable, Long elementId);
    Page<CommentElement> findByUserId(Pageable pageable, Long userId);
    Page<CommentElement> findByContentContainingIgnoreCase(Pageable pageable, String content);
} 