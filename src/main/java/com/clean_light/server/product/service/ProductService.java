package com.clean_light.server.product.service;

import com.clean_light.server.product.domain.Product;
import com.clean_light.server.product.repository.ProductRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public Product search(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));
    }

    public List<Product> searchPage(int page) {
        int productUnit = 20;
        PageRequest pageRequest = PageRequest.of(page, productUnit);

        return productRepository.findAll(pageRequest).getContent();
    }
}
