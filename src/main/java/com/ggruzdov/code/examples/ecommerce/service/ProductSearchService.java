package com.ggruzdov.code.examples.ecommerce.service;

import com.ggruzdov.code.examples.ecommerce.request.Pagination;
import com.ggruzdov.code.examples.ecommerce.request.ProductFilterSearchRequest;
import com.ggruzdov.code.examples.ecommerce.request.ProductFullTextSearchRequest;
import com.ggruzdov.code.examples.ecommerce.response.ProductSearchResponse;
import com.ggruzdov.code.examples.ecommerce.request.SortCriteria;
import com.ggruzdov.code.examples.ecommerce.model.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class ProductSearchService {

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
    public Page<ProductSearchResponse> search(ProductFullTextSearchRequest request) {
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
    public Page<ProductSearchResponse> search(ProductFilterSearchRequest request) {
        var queryBuilder = new StringBuilder("SELECT * FROM products p WHERE p.category_id = :categoryId");
        var params = new HashMap<String, Object>();
        params.put("categoryId", request.categoryId());

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

        request.filters().forEach((attribute, condition) -> {
            switch (condition.operator()) {
                case "eq" -> queryBuilder.append(format(" AND (p.attributes->>'%s') = %s::text", attribute, condition.value()));
                case "ne" -> queryBuilder.append(format(" AND (p.attributes->>'%s') != %s", attribute, condition.value()));
                case "gt" -> queryBuilder.append(format(" AND (p.attributes->>'%s')::NUMERIC > %s", attribute, condition.value()));
                case "lt" -> queryBuilder.append(format(" AND (p.attributes->>'%s')::NUMERIC < %s", attribute, condition.value()));
                case "gte" -> queryBuilder.append(format(" AND (p.attributes->>'%s')::NUMERIC >= %s", attribute, condition.value()));
                case "lte" -> queryBuilder.append(format(" AND (p.attributes->>'%s')::NUMERIC <= %s", attribute, condition.value()));
                case "in" -> {
                    var values = condition.values()
                        .stream()
                        .map(s -> format("'%s'", s))
                        .collect(Collectors.joining(","));
                    queryBuilder.append(format(" AND (p.attributes->>'%s') IN (%s)", attribute, values));
                }
                case "between" ->
                    queryBuilder.append(format(" AND (p.attributes->>'%s')::NUMERIC BETWEEN %s AND %s", attribute, condition.fromValue(), condition.toValue()));
                case "contains" ->
                    queryBuilder.append(format(" AND (p.attributes->>'%s') ILIKE '%%%s%%'", attribute, condition.value()));
                default -> throw new IllegalArgumentException("Unsupported operator: " + condition.operator());
            }
        });

        return createQueryAndExecute(queryBuilder, params, request.sort(), request.pagination());
    }

    private Page<ProductSearchResponse> createQueryAndExecute(
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

        return new PageImpl<>(result, page, total);
    }
}
