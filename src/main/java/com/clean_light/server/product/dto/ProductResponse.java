package com.clean_light.server.product.dto;

import com.clean_light.server.product.domain.Product;
import lombok.Data;

@Data
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private String imgUrl;
    private int price;

    public ProductResponse(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.imgUrl = product.getImgUrl();
        this.price = product.getPrice();
    }
}
