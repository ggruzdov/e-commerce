package com.github.ggruzdov.ecommerce.controller;

import com.github.ggruzdov.ecommerce.request.ProductFilterSearchRequest;
import com.github.ggruzdov.ecommerce.request.ProductFullTextSearchRequest;
import com.github.ggruzdov.ecommerce.response.ProductSearchResponse;
import com.github.ggruzdov.ecommerce.service.ProductSearchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedModel;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/products/search")
public class ProductSearchController {

    private final ProductSearchService productSearchService;

    @PostMapping
    public PagedModel<ProductSearchResponse> search(@Valid @RequestBody ProductFilterSearchRequest request) {
        log.info("Searching products: categoryId = {}, brand = {}, price = {}, filters: {}",
            request.categoryId(), request.brand(), request.price(), request.filters()
        );
        return productSearchService.search(request);
    }

    @PostMapping("/full-text")
    public PagedModel<ProductSearchResponse> search(@Valid @RequestBody ProductFullTextSearchRequest request) {
        log.info("Searching products by phrase: {}", request.phrase());
        return productSearchService.search(request);
    }
}
