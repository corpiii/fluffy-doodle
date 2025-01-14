package com.clean_light.server.cart.controller;

import com.clean_light.server.cart.domain.CartItem;
import com.clean_light.server.cart.dto.CartItemResponse;
import com.clean_light.server.cart.service.CartService;
import com.clean_light.server.global.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @RequestMapping(value = "/api/user/cart", method = RequestMethod.HEAD)
    public ResponseEntity<ApiResponse<List<CartItemResponse>>> fetchCartItemList(HttpServletRequest request) {
        try {
            String accessToken = request.getParameter(HttpHeaders.AUTHORIZATION).substring(7);
            List<CartItem> cartItemList = cartService.fetchCartItemListBy(accessToken);
            List<CartItemResponse> responseList = cartItemList.stream().map(CartItemResponse::new).toList();

            return ResponseEntity.ok(new ApiResponse<>(true, responseList, ""));
        } catch (Exception exception) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, null, exception.getMessage()));
        }
    }

}
