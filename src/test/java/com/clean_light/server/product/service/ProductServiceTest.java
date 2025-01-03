package com.clean_light.server.product.service;

import com.clean_light.server.product.domain.Product;
import com.clean_light.server.product.repository.ProductRepository;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ProductServiceTest {
    @Autowired ProductRepository productRepository;

    @Test
    @DisplayName("상품 조회")
    void searchProductTest() {
        /* given */
        Long productId = 1L;
        String productName = "test1";
        String productDescription = productName + " description";
        Product testProduct = Product.builder().id(productId)
                .name(productName)
                .description(productDescription)
                .imgUrl("")
                .build();

        productRepository.save(testProduct);

        /* when */
        Product product = productRepository.findById(productId).orElseThrow();

        /* then */
        Assertions.assertThat(product.getName()).isEqualTo(productName);
        Assertions.assertThat(product.getDescription()).isEqualTo(productDescription);
    }
}