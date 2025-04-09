package com.periodic.backend.repository.specification;

import org.springframework.util.StringUtils;

import com.periodic.backend.domain.entity.ViewedPodcast;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class ViewedPodcastSpecification extends BaseSpecification<ViewedPodcast> {
	private static final long serialVersionUID = 1L;

	public ViewedPodcastSpecification(String searchTerm, String[] sortBy, String[] sortDirection, Boolean active) {
		super(searchTerm, sortBy, sortDirection, active);
	}

	@Override
	protected Predicate createSearchPredicate(Root<ViewedPodcast> root, CriteriaBuilder criteriaBuilder) {
		if (!StringUtils.hasText(searchTerm)) {
            return criteriaBuilder.conjunction();
        }

        String searchPattern = searchTerm.toLowerCase();
        
        return criteriaBuilder.like(criteriaBuilder.lower(root.get("user").get("email")), searchPattern);
	}
} 