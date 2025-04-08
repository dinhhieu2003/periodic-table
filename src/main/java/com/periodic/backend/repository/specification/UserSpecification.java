package com.periodic.backend.repository.specification;

import org.springframework.util.StringUtils;

import com.periodic.backend.domain.entity.User;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class UserSpecification extends BaseSpecification<User> {
    private static final long serialVersionUID = 1L;

    public UserSpecification(String searchTerm, String[] sortBy, String[] sortDirection, Boolean active) {
        super(searchTerm, sortBy, sortDirection, active);
    }

    @Override
    protected Predicate createSearchPredicate(Root<User> root, CriteriaBuilder criteriaBuilder) {
        if (!StringUtils.hasText(searchTerm)) {
            return criteriaBuilder.conjunction();
        }

        String searchPattern = "%" + searchTerm.toLowerCase() + "%";
        
        return criteriaBuilder.or(
            criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), searchPattern),
            criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), searchPattern)
        );
    }
}