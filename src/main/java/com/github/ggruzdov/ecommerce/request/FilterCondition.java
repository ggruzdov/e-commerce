package com.github.ggruzdov.ecommerce.request;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record FilterCondition(
    @NotNull
    String operator,      // eq, ne, gt, gte, lt, lte, in, contains, between
    Object value,
    List<String> values,  // for 'in' operator
    Object fromValue,     // for 'between' operator
    Object toValue        // for 'between' operator
) {
}