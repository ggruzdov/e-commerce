package com.ggruzdov.code.examples.ecommerce;

import com.ggruzdov.code.examples.ecommerce.dto.ProductSearchRequest;
import com.ggruzdov.code.examples.ecommerce.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ECommerceApplicationTests {

    @Autowired
    private ProductService productService;

    @Test
    void test() {
        var request = new ProductSearchRequest(
            2,
            null,
            null,
            null,
            null
        );

        var product = productService.search(request);
        var text = "test";
    }

}
