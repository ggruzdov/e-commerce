package com.ggruzdov.code.examples.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Map;

public record ProductSearchRequest(
    @NotNull
    Integer categoryId,
    PriceRange price,
    Map<String, FilterCondition> filters,
    List<SortCriteria> sort,
    Pagination pagination
) {
    public ProductSearchRequest {
        if (pagination == null) {
            pagination = new Pagination(1, 20);
        }
    }

    public record FilterCondition(
        @NotNull
        String operator, // eq, ne, gt, gte, lt, lte, in, contains, between
        Object value,
        List<Object> values, // for 'in' operator
        Object fromValue, // for 'between' operator
        Object toValue   // for 'between' operator
    ) {
    }

    public record PriceRange(
        Integer min,
        Integer max
    ) {
    }

    public record SortCriteria(
        @NotBlank
        String field,
        @NotBlank
        String order // asc, desc
    ) {
    }

    public record Pagination(
        @NotNull
        Integer page,
        @NotNull
        Integer limit
    ) {
    }
}
