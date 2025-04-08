package com.periodic.backend.repository.specification;

import org.springframework.util.StringUtils;

import com.periodic.backend.domain.entity.Scientist;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class ScientistSpecification extends BaseSpecification<Scientist> {
	private static final long serialVersionUID = 1L;

	public ScientistSpecification(String searchTerm, String[] sortBy, String[] sortDirection, Boolean active) {
        super(searchTerm, sortBy, sortDirection, active);
    }

    @Override
    protected Predicate createSearchPredicate(Root<Scientist> root, CriteriaBuilder criteriaBuilder) {
        if (!StringUtils.hasText(searchTerm)) {
            return criteriaBuilder.conjunction();
        }

        String searchPattern = "%" + searchTerm.toLowerCase() + "%";
        
        return criteriaBuilder.or(
            criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), searchPattern),
            criteriaBuilder.like(criteriaBuilder.lower(root.get("nationality")), searchPattern)
        );
    }
} 