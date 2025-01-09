package com.ggruzdov.code.examples.ecommerce.response;

import com.ggruzdov.code.examples.ecommerce.model.Product;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Map;

public record ProductSearchResponse(
    Integer id,
    String name,
    String brand,
    String description,
    BigDecimal price,
    BigDecimal weight,
    Map<String, Object> attributes,
    Instant createdAt
) {
    public static ProductSearchResponse from(Product product) {
        return new ProductSearchResponse(
            product.getId(),
            product.getName(),
            product.getBrand(),
            product.getDescription(),
            BigDecimal.valueOf(product.getPrice()).divide(BigDecimal.valueOf(100), 2, RoundingMode.UNNECESSARY),
            product.getWeight(),
            product.getAttributes(),
            product.getCreatedAt()
        );
    }
}
