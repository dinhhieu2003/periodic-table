package com.periodic.backend.repository.specification;

import org.springframework.util.StringUtils;

import com.periodic.backend.domain.entity.Podcast;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class PodcastSpecification extends BaseSpecification<Podcast> {
	private static final long serialVersionUID = 1L;

	public PodcastSpecification(String searchTerm, String[] sortBy, String[] sortDirection, Boolean active) {
        super(searchTerm, sortBy, sortDirection, active);
    }

    @Override
    protected Predicate createSearchPredicate(Root<Podcast> root, CriteriaBuilder criteriaBuilder) {
        if (!StringUtils.hasText(searchTerm)) {
            return criteriaBuilder.conjunction();
        }

        String searchPattern = "%" + searchTerm.toLowerCase() + "%";
        
        return criteriaBuilder.or(
            criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), searchPattern),
            criteriaBuilder.like(criteriaBuilder.lower(root.get("element").get("name")), searchPattern),
            criteriaBuilder.like(criteriaBuilder.lower(root.get("element").get("symbol")), searchPattern)
        );
    }
} 