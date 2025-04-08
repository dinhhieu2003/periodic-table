package com.periodic.backend.repository.specification;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseSpecification<T> implements Specification<T> {
	private static final long serialVersionUID = 1L;
	
	protected String searchTerm;
    protected String[] sortBy;
    protected String[] sortDirection;
    protected Boolean active;

    public BaseSpecification(String searchTerm, String[] sortBy, String[] sortDirection, Boolean active) {
        this.searchTerm = searchTerm;
        this.sortBy = sortBy;
        this.sortDirection = sortDirection;
        this.active = active;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();
        
        if (active != null) {
            predicates.add(criteriaBuilder.equal(root.get("isActive"), active));
        }
        
        if (StringUtils.hasText(searchTerm)) {
            predicates.add(createSearchPredicate(root, criteriaBuilder));
        }
        
        if (sortBy != null && sortBy.length > 0) {
            query.orderBy(createOrders(root, criteriaBuilder));
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    protected abstract Predicate createSearchPredicate(Root<T> root, CriteriaBuilder criteriaBuilder);
    
    protected List<Order> createOrders(Root<T> root, CriteriaBuilder criteriaBuilder) {
        List<Order> orders = new ArrayList<>();
        
        for (int i = 0; i < sortBy.length; i++) {
            String field = sortBy[i];
            String direction = (sortDirection != null && i < sortDirection.length) ? sortDirection[i] : "asc";
            
            String[] path = field.split("\\.");
            jakarta.persistence.criteria.Path<Object> propertyPath = root.get(path[0]);
            
            for (int j = 1; j < path.length; j++) {
                propertyPath = propertyPath.get(path[j]);
            }
            
            if ("desc".equalsIgnoreCase(direction)) {
                orders.add(criteriaBuilder.desc(propertyPath));
            } else {
                orders.add(criteriaBuilder.asc(propertyPath));
            }
        }
        
        return orders;
    }
} 