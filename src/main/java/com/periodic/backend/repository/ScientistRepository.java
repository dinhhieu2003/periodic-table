package com.periodic.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.periodic.backend.domain.entity.Scientist;

public interface ScientistRepository extends JpaRepository<Scientist, Long>, JpaSpecificationExecutor<Scientist>{
	public Page<Scientist> findByNameContainingIgnoreCase(Pageable pageable, String name);
}
