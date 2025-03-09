package com.github.ggruzdov.ecommerce.component;

import com.github.ggruzdov.ecommerce.request.FilterCondition;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
public class FilterConditionValidator {

    public boolean isValidCondition(FilterCondition condition) {
        if (Operators.SINGLE_VALUE_OPERATORS.contains(condition.operator()) && condition.value() == null) {
            return false;
        }

        if (Operators.BETWEEN.equals(condition.operator()) && (condition.fromValue() == null || condition.toValue() == null)) {
            return false;
        }

        if (Operators.IN.equals(condition.operator()) && CollectionUtils.isEmpty(condition.values())) {
            return false;
        }

        return true;
    }
}
