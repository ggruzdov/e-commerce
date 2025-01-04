package com.ggruzdov.code.examples.ecommerce.service;

import com.ggruzdov.code.examples.ecommerce.dto.ProductSearchRequest;
import com.ggruzdov.code.examples.ecommerce.dto.ProductSearchResponse;
import com.ggruzdov.code.examples.ecommerce.model.AttributeDefinition;
import com.ggruzdov.code.examples.ecommerce.model.Product;
import com.ggruzdov.code.examples.ecommerce.model.ProductAttribute;
import com.ggruzdov.code.examples.ecommerce.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class ProductService {

    private final EntityManager entityManager;
    private final ProductRepository productRepository;

//    public List<Product> search() {
//        Query nativeQuery = entityManager.createNativeQuery("""
//            select
//             p.id, p.sku, p.name, p.category_id, p.description, p.price, p.weight, p.created_at, p.updated_at,
//             pa.id as paId, pa.product_id, pa.attribute_def_id, pa.value
//--             ad.id as asId, ad.category_id, ad.name, ad.type, ad.is_required, ad.validation_rules, ad.display_order
//            from products p
//            join attribute_definitions ad on p.category_id = ad.category_id
//            join product_attributes pa on ad.id = pa.attribute_def_id
//            where p.category_id = 2
//                and exists(
//                    select 1 from product_attributes pa2
//                    join attribute_definitions ad2 on pa2.attribute_def_id = ad2.id
//                    where pa2.product_id = p.id
//                    and ad2.name = 'RAM'
//                    and pa2.value = '16'
//                )
//         """, Product.class);
//
//        return nativeQuery.getResultList();
//    }

    @Transactional(readOnly = true)
    public Page<ProductSearchResponse> search(ProductSearchRequest request) {
        Specification<Product> spec = buildSpecification(request);

        Sort sort = buildSort(request.sort());
        PageRequest pageRequest = PageRequest.of(
            request.pagination().page() - 1,
            request.pagination().limit(),
            sort
        );

        return productRepository.findAll(spec, pageRequest).map(ProductSearchResponse::from);
    }

    private Specification<Product> buildSpecification(ProductSearchRequest request) {
        return (root, query, cb) -> {
            var paJoin = root.fetch("productAttributes");
            var adJoin = paJoin.fetch("attributeDef");

            List<Predicate> predicates = new ArrayList<>();

            // Add category filter
            predicates.add(cb.equal(root.get("categoryId"), request.categoryId()));

            // Add price range filter if present
            if (request.price() != null) {
                if (request.price().min() != null) {
                    predicates.add(cb.ge(root.get("price"), request.price().min()));
                }
                if (request.price().max() != null) {
                    predicates.add(cb.le(root.get("price"), request.price().max()));
                }
            }

//            // Add attribute filters
//            if (!CollectionUtils.isEmpty(request.filters())) {
//                request.filters().forEach((attribute, condition) -> {
//                    var attrFilter = switch (condition.operator()) {
//                        case "eq" -> pa.equal(root.get(""), condition.value());
//                        case "ne" -> cb.notEqual(path, condition.value());
//                        case "gt" -> cb.greaterThan(path, condition.value().toString());
//                        case "gte" -> cb.greaterThanOrEqualTo(path, condition.value().toString());
//                        case "lt" -> cb.lessThan(path, condition.value().toString());
//                        case "lte" -> cb.lessThanOrEqualTo(path, condition.value().toString());
//                        case "contains" -> cb.like(path, "%" + condition.value() + "%");
//                        case "in" -> path.in(condition.values());
//                        case "between" -> cb.between(path,
//                            condition.fromValue().toString(),
//                            condition.toValue().toString());
//                        default -> throw new IllegalArgumentException("Unsupported operator: " + condition.operator());
//                    };
//
//                    predicates.add(attrFilter);
//                });
//            }

            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

    private Sort buildSort(List<ProductSearchRequest.SortCriteria> sortCriteria) {
        if (CollectionUtils.isEmpty(sortCriteria)) {
            return Sort.unsorted();
        }

        List<Sort.Order> orders = sortCriteria.stream()
            .map(criteria -> new Sort.Order(
                Sort.Direction.fromString(criteria.order()),
                criteria.field()
            )).toList();

        return Sort.by(orders);
    }
}
