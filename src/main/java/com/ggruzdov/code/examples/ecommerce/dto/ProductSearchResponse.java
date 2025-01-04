package com.ggruzdov.code.examples.ecommerce.dto;

import com.ggruzdov.code.examples.ecommerce.model.Product;
import com.ggruzdov.code.examples.ecommerce.model.ProductAttribute;

import java.math.BigDecimal;
import java.util.List;

public record ProductSearchResponse(
    Integer id,
    String name,
    String description,
    Double price,
    BigDecimal weight,
    List<Attribute> attributes
) {
    public static ProductSearchResponse from(Product product) {
        return new ProductSearchResponse(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getPrice() / 100.0,
            product.getWeight(),
            product.getProductAttributes()
                .stream()
                .map(Attribute::from)
                .toList()
        );
    }

    public record Attribute(
        String name,
        String value
    ) {
        public static Attribute from(ProductAttribute attribute) {
            return new Attribute(
                attribute.getAttributeDef().getName(),
                attribute.getValue()
            );
        }
    }
}
