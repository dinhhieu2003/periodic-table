package com.periodic.backend.repository.specification;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;
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

            Path<?> propertyPath = buildPropertyPath(root, field);

            Order order;

            if (Number.class.isAssignableFrom(propertyPath.getJavaType())) {
                @SuppressWarnings("unchecked")
                Path<Number> numberPath = (Path<Number>) propertyPath;

                // Dùng toInteger để sort đúng kiểu số
                order = "desc".equalsIgnoreCase(direction)
                        ? criteriaBuilder.desc(criteriaBuilder.toInteger(numberPath))
                        : criteriaBuilder.asc(criteriaBuilder.toInteger(numberPath));
            } else {
                order = "desc".equalsIgnoreCase(direction)
                        ? criteriaBuilder.desc(propertyPath)
                        : criteriaBuilder.asc(propertyPath);
            }

            orders.add(order);
        }

        return orders;
    }

    private Path<?> buildPropertyPath(Root<T> root, String field) {
        String[] parts = field.split("\\.");
        Path<?> path = root.get(parts[0]);
        for (int j = 1; j < parts.length; j++) {
            path = path.get(parts[j]);
        }
        return path;
    }

} 