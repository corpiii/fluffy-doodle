package com.clean_light.server.cart.domain;

import com.clean_light.server.auth.user.domain.User;
import com.clean_light.server.product.domain.Product;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    @Id
    @GeneratedValue
    @Column(name = "cart-item_id")
    private Long id;

    @OneToOne
    private Product product;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User oner;

    private int price;

    private int amount;

    private int discount;

    public void addAmount(int amount) {
        this.amount += amount;
    }
}
