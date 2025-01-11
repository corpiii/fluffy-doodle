package com.clean_light.server.cart.service;

import com.clean_light.server.auth.jwt.domain.TokenType;
import com.clean_light.server.auth.jwt.dto.UserTokenInfo;
import com.clean_light.server.auth.jwt.service.JwtService;
import com.clean_light.server.auth.user.domain.User;
import com.clean_light.server.auth.user.repository.UserRepository;
import com.clean_light.server.auth.user.service.UserAuthService;
import com.clean_light.server.cart.domain.CartItem;
import com.clean_light.server.product.domain.Product;
import com.clean_light.server.product.service.ProductService;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final ProductService productService;

    public List<CartItem> fetchCartItemListBy(String accessToken) throws JsonProcessingException {
        User user = findUserByAccessToken(accessToken);

        return user.getCartItemList();
    }

    @Transactional
    public void addCartItem(String accessToken, Long productId) throws JsonProcessingException {
        User user = findUserByAccessToken(accessToken);
        Product targetProduct = productService.search(productId);
        CartItem cartItem = new CartItem(null, targetProduct, user, targetProduct.getPrice(), 1, 0);

        user.getCartItemList().add(cartItem);
    }

    private User findUserByAccessToken(String accessToken) throws JsonProcessingException {
        UserTokenInfo userTokenInfo = jwtService.decodeToken(accessToken);
        String loginId = userTokenInfo.getLoginId();

        if (!jwtService.isExist(loginId, TokenType.ACCESS)) {
            throw new IllegalArgumentException("해당 유저를 찾을 수 없습니다. 다시 로그인 해주세요.");
        }

        return userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다. 다시 로그인 해주세요."));
    }
}
