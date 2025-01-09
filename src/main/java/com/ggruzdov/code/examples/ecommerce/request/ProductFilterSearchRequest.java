package com.ggruzdov.code.examples.ecommerce.request;

import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Map;

public record ProductFilterSearchRequest(
    @NotNull
    Integer categoryId,
    String brand,
    PriceRange price,
    Map<String, FilterCondition> filters,
    SortCriteria sort,
    Pagination pagination
) {
    public ProductFilterSearchRequest {
        if (pagination == null) {
            pagination = Pagination.DEFAULT;
        }
        if (filters == null) {
            filters = Map.of();
        }
        if (sort == null) {
            sort = SortCriteria.DEFAULT;
        }
    }

    public record FilterCondition(
        @NotNull
        String operator,     // eq, ne, gt, gte, lt, lte, in, contains, between
        Object value,
        List<Object> values, // for 'in' operator
        Object fromValue,    // for 'between' operator
        Object toValue       // for 'between' operator
    ) {
    }

    public record PriceRange(
        Integer min,
        Integer max
    ) {
    }
}
