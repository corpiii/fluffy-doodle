package com.clean_light.server.product.service;

import com.clean_light.server.dummy.ProductDummy;
import com.clean_light.server.product.domain.Product;
import com.clean_light.server.product.repository.ProductRepository;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class ProductServiceTest {
    @Autowired ProductRepository productRepository;
    @Autowired ProductService productService;
    ProductDummy productDummy = new ProductDummy();

    @Test
    @Transactional
    @DisplayName("상품 조회")
    void searchProductTest() {
        /* given */
        Product testProduct = productDummy.getDummyList().get(0);
        Long productId = 13L;
        String productName = testProduct.getName();
        String productDescription = testProduct.getDescription();

        productRepository.save(testProduct);

        /* when */
        Product product = productService.search(productId);

        /* then */
        Assertions.assertThat(product.getName()).isEqualTo(productName);
        Assertions.assertThat(product.getDescription()).isEqualTo(productDescription);
    }
    
    @Test
    @Transactional
    @DisplayName("상품 목록 조회")
    public void searchProductList() {
        /* given */
        ProductDummy productDummy = new ProductDummy();
        List<Product> dummyList = productDummy.getDummyList();
        dummyList.forEach(dummy -> productRepository.save(dummy));

        /* when */
        List<Product> expected = productService.searchPage(0);

        /* then */
        Assertions.assertThat(expected).isEqualTo(dummyList);
    }
}