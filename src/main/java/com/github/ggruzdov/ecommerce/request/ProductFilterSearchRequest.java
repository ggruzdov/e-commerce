package com.github.ggruzdov.ecommerce.request;

import jakarta.validation.constraints.NotNull;

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
}
