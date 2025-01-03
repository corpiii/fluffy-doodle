package com.clean_light.server.product.controller;

import com.clean_light.server.global.ApiResponse;
import com.clean_light.server.product.domain.Product;
import com.clean_light.server.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product")
public class ProductController {
    private final ProductService productService;

    @GetMapping("/{id}")
    public ApiResponse<Product> searchProduct(@PathVariable Long id) {
        try {
            Product product = productService.search(id);

            return new ApiResponse<Product>(true, product, "");
        } catch (IllegalArgumentException exception) {
            return new ApiResponse<Product>(false, null, exception.getMessage());
        }
    }
}
