package com.ggruzdov.code.examples.ecommerce.request;

import jakarta.validation.constraints.NotBlank;

public record ProductFullTextSearchRequest(
    @NotBlank
    String phrase,
    SortCriteria sort,
    Pagination pagination
) {
    public ProductFullTextSearchRequest {
        if (pagination == null) {
            pagination = Pagination.DEFAULT;
        }
        if (sort == null) {
            sort = SortCriteria.DEFAULT;
        }
    }
}
