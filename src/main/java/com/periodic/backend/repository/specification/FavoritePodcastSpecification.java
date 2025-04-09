package com.periodic.backend.repository.specification;

import org.springframework.util.StringUtils;

import com.periodic.backend.domain.entity.FavoritePodcast;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class FavoritePodcastSpecification extends BaseSpecification<FavoritePodcast> {
	private static final long serialVersionUID = 1L;

	public FavoritePodcastSpecification(String searchTerm, String[] sortBy, String[] sortDirection, Boolean active) {
		super(searchTerm, sortBy, sortDirection, active);
	}

	@Override
	protected Predicate createSearchPredicate(Root<FavoritePodcast> root, CriteriaBuilder criteriaBuilder) {
		if (!StringUtils.hasText(searchTerm)) {
            return criteriaBuilder.conjunction();
        }

        String searchPattern = searchTerm.toLowerCase();
        
        return criteriaBuilder.like(criteriaBuilder.lower(root.get("user").get("email")), searchPattern);
	}
	
    
} 