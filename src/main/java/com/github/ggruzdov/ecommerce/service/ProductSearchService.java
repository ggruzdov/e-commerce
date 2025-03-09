package com.github.ggruzdov.ecommerce.service;

import com.github.ggruzdov.ecommerce.component.FilterConditionValidator;
import com.github.ggruzdov.ecommerce.component.Operators;
import com.github.ggruzdov.ecommerce.request.Pagination;
import com.github.ggruzdov.ecommerce.request.ProductFilterSearchRequest;
import com.github.ggruzdov.ecommerce.request.ProductFullTextSearchRequest;
import com.github.ggruzdov.ecommerce.response.ProductSearchResponse;
import com.github.ggruzdov.ecommerce.request.SortCriteria;
import com.github.ggruzdov.ecommerce.model.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductSearchService {

    private final FilterConditionValidator filterConditionValidator;
    private final AttributeDefinitionService attributeDefinitionService;
    private final EntityManager entityManager;

    /**
     * Products full text search based on product description field.
     * Since the method uses Postgres 'websearch_to_tsquery' function
     * there is not any phrase pre-cleaning operations as the function handles it.
     * <p>
     * The result query might look like:
     * SELECT * FROM products p
     * WHERE p.description @@ websearch_to_tsquery('Asus Intel i7')
     * ORDER BY p.created_at desc
     * FETCH FIRST 20 ROWS ONLY;
     * <p>
     * NOTE: there are actually two queries: one for total amount of rows(without sorting and pagination)
     *       and the second is a paginated 'real' query.
     *
     * @param request contains user search phrase, e.g. "Asus Intel i7".
     * @return paginated list of products corresponding to the search phrase.
     */
    public PagedModel<ProductSearchResponse> search(ProductFullTextSearchRequest request) {
        var queryBuilder = new StringBuilder(
            "SELECT * FROM products p WHERE p.description @@ websearch_to_tsquery(:phrase)"
        );
        var params = Map.<String, Object>of("phrase", request.phrase());

        return createQueryAndExecute(queryBuilder, params, request.sort(), request.pagination());
    }

    /**
     * Products search by set of filters.
     * Method dynamically builds an SQL query depending on specified filters.
     * <p>
     * The result query might look like:
     * SELECT * FROM products p
     * WHERE p.category_id = ?
     *   AND (p.attributes->>'processor') ILIKE '%intel%'
     *   AND (p.attributes->>'screen_size')::NUMERIC BETWEEN 14.0 AND 15.6
     *   AND (p.attributes->>'RAM')::NUMERIC >= 16
     *   AND (p.attributes->>'storage_type') IN ('SSD')
     *   ORDER BY p.price ASC
     *   OFFSET 100 ROWS
     *   FETCH NEXT 20 ROWS ONLY;
     * <p>
     * NOTE: there are actually two queries: one for total amount of rows(without sorting and pagination)
     *       and the second is a paginated 'real' query.
     *
     * @param request set of attributes to filter products.
     * @return paginated list of products corresponding to the set of filters.
     */
    public PagedModel<ProductSearchResponse> search(ProductFilterSearchRequest request) {
        var queryBuilder = new StringBuilder("SELECT * FROM products p WHERE p.category_id = :categoryId");
        var params = new HashMap<String, Object>();
        params.put("categoryId", request.categoryId());

        if (request.brand() != null) {
            queryBuilder.append(" AND brand = :brand");
            params.put("brand", request.brand());
        }

        // Price range filter
        if (request.price() != null) {
            if (request.price().min() != null) {
                queryBuilder.append(" AND p.price >= :minPrice");
                params.put("minPrice", request.price().min());
            }
            if (request.price().max() != null) {
                queryBuilder.append(" AND p.price <= :maxPrice");
                params.put("maxPrice", request.price().max());
            }
        }

        var categoryAttributeNames = attributeDefinitionService.getAttributeNames(request.categoryId());
        request.filters().forEach((attribute, condition) -> {
            if (!categoryAttributeNames.contains(attribute)) {
                throw new IllegalArgumentException("Invalid attribute name: " + attribute);
            }

            if (!filterConditionValidator.isValidCondition(condition)) {
                throw new IllegalArgumentException("Invalid value for condition: " + condition);
            }

            queryBuilder.append(" AND (p.attributes->>'");
            String operator = condition.operator();
            String paramName = operator + "_" + attribute;
            switch (operator) {
                case "eq", "ne" -> {
                    var operatorSign = Operators.getSign(operator);
                    queryBuilder.append(attribute).append("') ").append(operatorSign).append(" cast(:").append(paramName).append(" as text)");
                    params.put(paramName, condition.value());
                }
                case "gt", "lt", "gte", "lte" -> {
                    var operatorSign = Operators.getSign(operator);
                    queryBuilder.append(attribute).append("')::NUMERIC ").append(operatorSign).append(" :").append(paramName);
                    params.put(paramName, condition.value());
                }
                case "in" -> {
                    List<String> values = condition.values();
                    // Create separate parameter for each value
                    List<String> paramNames = new ArrayList<>();
                    for (int i = 0; i < values.size(); i++) {
                        String inParamName = paramName + "_" + i;
                        paramNames.add(":" + inParamName);
                        params.put(inParamName, values.get(i));
                    }

                    queryBuilder.append(attribute)
                        .append("') IN (")
                        .append(String.join(",", paramNames))
                        .append(")");
                }
                case "between" -> {
                    String fromParamName = "between_from_" + attribute;
                    String toParamName = "between_to_" + attribute;

                    queryBuilder.append(attribute)
                        .append("')::NUMERIC BETWEEN :")
                        .append(fromParamName)
                        .append(" AND :")
                        .append(toParamName);

                    params.put(fromParamName, condition.fromValue());
                    params.put(toParamName, condition.toValue());
                }
                case "contains" -> {
                    queryBuilder.append(attribute)
                        .append("') ILIKE :")
                        .append(paramName);

                    params.put(paramName, "%" + condition.value() + "%");
                }
                default -> throw new IllegalArgumentException("Unsupported operator: " + operator);
            }
        });

        return createQueryAndExecute(queryBuilder, params, request.sort(), request.pagination());
    }

    private PagedModel<ProductSearchResponse> createQueryAndExecute(
        StringBuilder queryBuilder,
        Map<String, Object> params,
        SortCriteria sort,
        Pagination pagination
    ) {
        // Fetch total count for pagination result
        var countSql = queryBuilder.toString().replaceFirst("SELECT \\*", "SELECT COUNT(1)");
        var countQuery = entityManager.createNativeQuery(countSql, Long.class);
        params.forEach(countQuery::setParameter);
        long total = (long) countQuery.getSingleResult();

        // Sorting
        queryBuilder.append(" ORDER BY p.").append(sort.field()).append(" ").append(sort.order());

        // Create query
        Query query = entityManager.createNativeQuery(queryBuilder.toString(), Product.class);
        params.forEach(query::setParameter);

        // Query limit offset
        query.setFirstResult((pagination.page() - 1) * pagination.limit());
        query.setMaxResults(pagination.limit());

        @SuppressWarnings("unchecked")
        var result = ((List<Product>) query.getResultList())
            .stream()
            .map(ProductSearchResponse::from)
            .toList();

        var page = PageRequest.of(
            pagination.page() - 1,
            pagination.limit(),
            Sort.Direction.fromString(sort.order()), sort.field()
        );

        return new PagedModel<>(new PageImpl<>(result, page, total));
    }
}
