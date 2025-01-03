package com.clean_light.server.product.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import org.springframework.data.annotation.Id;

@Entity
public class Product {
    @Id
    @GeneratedValue
    @Column(name = "product_id")
    private Long id;

    private String name;

    private String description;

    private String imgUrl;
}
