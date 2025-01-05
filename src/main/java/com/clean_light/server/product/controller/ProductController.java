package com.clean_light.server.product.controller;

import com.clean_light.server.global.ApiResponse;
import com.clean_light.server.product.domain.Product;
import com.clean_light.server.product.service.ProductService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product")
public class ProductController {
    /*
    * 상품 등록, 삭제는 admin 에서 처리.
    * */

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

    @GetMapping("/products/{page}")
    public ApiResponse<List<Product>> searchProductList(@PathVariable(value = "page") int page) {
        try {
            List<Product> products = productService.searchPage(page);

            return new ApiResponse<List<Product>>(true, products, "");
        } catch (IllegalArgumentException exception) {
            return new ApiResponse<List<Product>>(false, null, exception.getMessage());
        }
    }
}
