package com.clean_light.server.cart.dto;

import com.clean_light.server.cart.domain.CartItem;
import com.clean_light.server.product.dto.ProductResponse;
import lombok.Data;

@Data
public class CartItemResponse {
    private ProductResponse product;
    private int price;
    private int amount;
    private int discount;

    public CartItemResponse(CartItem cartItem) {
        this.product = new ProductResponse(cartItem.getProduct());
        this.price = cartItem.getPrice();
        this.amount = cartItem.getAmount();
        this.discount = cartItem.getDiscount();
    }
}
