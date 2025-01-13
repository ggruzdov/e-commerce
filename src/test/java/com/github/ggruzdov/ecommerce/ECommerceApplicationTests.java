package com.github.ggruzdov.ecommerce;

import com.github.ggruzdov.ecommerce.request.Pagination;
import com.github.ggruzdov.ecommerce.request.ProductFilterSearchRequest;
import com.github.ggruzdov.ecommerce.request.ProductFullTextSearchRequest;
import com.github.ggruzdov.ecommerce.request.SortCriteria;
import com.github.ggruzdov.ecommerce.service.ProductSearchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class ECommerceApplicationTests {

    @Autowired
    private ProductSearchService productSearchService;

    @Test
    void searchLaptopsByAttributes() {
        // Given
        var request = new ProductFilterSearchRequest(
            2, // laptops
            null,
            null,
            Map.of(
                "RAM", new ProductFilterSearchRequest.FilterCondition("gte", 16, null, null, null),
                "processor", new ProductFilterSearchRequest.FilterCondition("contains", "intel", null, null, null),
                "screen_size", new ProductFilterSearchRequest.FilterCondition("between", null, null, 14.0, 15.6),
                "storage_type", new ProductFilterSearchRequest.FilterCondition("in", null, List.of("SSD"), null, null)
            ),
            new SortCriteria("price", "asc"),
            new Pagination(1, 20)
        );

        // When
        var result = productSearchService.search(request);

        // Then
        assertFalse(result.isEmpty());
        result.forEach(laptop -> assertTrue(((String) laptop.attributes().get("processor")).toLowerCase().contains("intel")));
        // Check sort by price asc
        assertTrue(result.getContent().getFirst().price().compareTo(result.getContent().getLast().price()) < 1);
    }

    @Test
    void searchLaptopsByPhrase() {
        // Given(intentionally dummy phrase)
        var phrase = " Dell!! !!   Intel@@#   i7   ";

        // When
        var result = productSearchService.search(new ProductFullTextSearchRequest(phrase, null, null));

        // Then
        assertFalse(result.isEmpty());
        result.forEach(laptop -> {
            assertEquals("Dell", laptop.brand());
            assertTrue(((String) laptop.attributes().get("processor")).toLowerCase().contains("intel"));
        } );
        // Check default sort by created_at desc
        assertTrue(result.getContent().getFirst().createdAt().isAfter(result.getContent().getLast().createdAt()));
    }
}
