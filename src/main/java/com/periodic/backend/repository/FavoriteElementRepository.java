package com.periodic.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.periodic.backend.domain.entity.FavoriteElement;

@Repository
public interface FavoriteElementRepository extends JpaRepository<FavoriteElement, Long>, JpaSpecificationExecutor<FavoriteElement> {
	Optional<FavoriteElement> findByUser_IdAndElement_Id(Long userId, Long elementId);
	Optional<FavoriteElement> findByElementId(Long elementId);
}
