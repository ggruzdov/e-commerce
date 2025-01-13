package com.github.ggruzdov.ecommerce.request;

import jakarta.validation.constraints.NotNull;

public record Pagination(
    @NotNull
    Integer page,
    @NotNull
    Integer limit
) {
    public static Pagination DEFAULT = new Pagination(1, 20);
}