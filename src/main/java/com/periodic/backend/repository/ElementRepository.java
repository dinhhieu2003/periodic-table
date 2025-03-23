package com.periodic.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.periodic.backend.domain.entity.Element;

public interface ElementRepository extends JpaRepository<Element, Long> {
	boolean existsByNameIgnoreCase(String name);
	boolean existsBySymbolIgnoreCase(String symbol);
	Page<Element> findByNameContainingIgnoreCase(Pageable pageable, String name);
}
