package com.ggruzdov.code.examples.ecommerce.request;

import jakarta.validation.constraints.NotBlank;

public record SortCriteria(
    @NotBlank
    String field, // created_at, price
    @NotBlank
    String order  // asc, desc
) {
    public static SortCriteria DEFAULT = new SortCriteria("created_at", "desc");
}