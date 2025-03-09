package com.github.ggruzdov.ecommerce.component;

import java.util.Map;
import java.util.Set;

public class Operators {
    private Operators() {}
    
    public static final String EQ = "eq";
    public static final String NE = "ne";
    public static final String GT = "gt";
    public static final String LT = "lt";
    public static final String GTE = "gte";
    public static final String LTE = "lte";
    public static final String CONTAINS = "contains";
    public static final String BETWEEN = "between";
    public static final String IN = "in";

    public static final Set<String> SINGLE_VALUE_OPERATORS = Set.of(EQ, NE, GT, LT, GTE, LTE, CONTAINS);

    private static final Map<String, String> OPERATOR_SIGNS = Map.of(
        EQ, "=",
        NE, "!=",
        GT, ">",
        LT, "<",
        GTE, ">=",
        LTE, "<="
    );

    public static String getSign(String operator) {
        return OPERATOR_SIGNS.get(operator)
            .describeConstable()
            .orElseThrow(() -> new IllegalArgumentException("Unknown operator: " + operator));
    }
}
