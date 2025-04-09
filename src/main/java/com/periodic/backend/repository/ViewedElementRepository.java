package com.periodic.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.periodic.backend.domain.entity.ViewedElement;

@Repository
public interface ViewedElementRepository extends JpaRepository<ViewedElement, Long>, JpaSpecificationExecutor<ViewedElement> {
	
}
