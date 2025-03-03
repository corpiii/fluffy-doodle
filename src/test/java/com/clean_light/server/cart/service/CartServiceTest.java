package com.clean_light.server.cart.service;

import com.clean_light.server.auth.jwt.dto.UserTokenInfo;
import com.clean_light.server.auth.jwt.service.JwtService;
import com.clean_light.server.auth.user.domain.User;
import com.clean_light.server.auth.user.dto.UserAuthToken;
import com.clean_light.server.auth.user.repository.UserRepository;
import com.clean_light.server.auth.user.service.UserAuthService;
import com.clean_light.server.cart.domain.CartItem;
import com.clean_light.server.dummy.ProductDummy;
import com.clean_light.server.product.domain.Product;
import com.clean_light.server.product.repository.ProductRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class CartServiceTest {
    @Autowired private CartService cartService;
    @Autowired private JwtService jwtService;
    @Autowired private UserAuthService userAuthService;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private ProductRepository productRepository;

    private final String testLoginId = "loginId";
    private String accessToken;

    @BeforeEach
    void initUser() throws JsonProcessingException {
        User testUser = User.builder()
                .loginId(testLoginId)
                .password(passwordEncoder.encode("password"))
                .email("email")
                .nickName("nickName")
                .build();

        userAuthService.join(testUser);

        User willLoginUser = User.builder()
                .loginId(testLoginId)
                .password("password")
                .build();

        UserAuthToken authToken = userAuthService.login(willLoginUser);
        accessToken = authToken.getAccessToken();
    }

    @BeforeEach
    void initProduct() {
        new ProductDummy().getDummyList().forEach(product -> productRepository.save(product));
    }

    @Test
    @Transactional
    @DisplayName("장바구니 목록 가져오기")
    void fetchCartItemList() throws JsonProcessingException {
        /* given */
        User joinedUser = userAuthService.findByLoginId(testLoginId);
        Product expectedProduct = new ProductDummy().getDummyList().get(0);
        CartItem cartItem = new CartItem(null, expectedProduct, joinedUser, expectedProduct.getPrice(), 10, 0);
        joinedUser.getCartItemList().add(cartItem);

        /* when */
        String accessToken = jwtService.generateAccessToken(UserTokenInfo.from(joinedUser));
        List<CartItem> cartItemList = cartService.fetchCartItemListBy(accessToken);
        Product actualProduct = cartItemList.get(0).getProduct();

        /* then */
        Assertions.assertThat(cartItemList.size()).isEqualTo(1);
        Assertions.assertThat(actualProduct.getName()).isEqualTo(expectedProduct.getName());
        Assertions.assertThat(actualProduct.getDescription()).isEqualTo(expectedProduct.getDescription());
    }

    @Test
    @Transactional
    @DisplayName("장바구니 상품 추가")
    void addCartItem() throws JsonProcessingException {
        // given
        UserTokenInfo userTokenInfo = jwtService.decodeToken(accessToken);
        User user = userAuthService.findByLoginId(userTokenInfo.getLoginId());
        List<Product> expectedProductList = new ProductDummy().getDummyList();
        expectedProductList.forEach(product -> productRepository.save(product));

        // when
        expectedProductList.forEach(product -> {
            try {
                cartService.addCartItem(accessToken, product.getId());
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });

        // then
        List<Product> actualList = user.getCartItemList().stream().map(CartItem::getProduct).toList();

        Assertions.assertThat(actualList).isEqualTo(expectedProductList);
    }

    @Test
    @Transactional
    @DisplayName("장바구니 상품 삭제")
    void deleteCartItem() throws JsonProcessingException {
        /* given */
        User joinedUser = userAuthService.findByLoginId(testLoginId);
        Product expectedProduct = new ProductDummy().getDummyList().get(0);
        CartItem cartItem = new CartItem(null, expectedProduct, joinedUser, expectedProduct.getPrice(), 10, 0);
        joinedUser.getCartItemList().add(cartItem);

        /* when */
        List<Product> productListBeforeDelete = joinedUser.getCartItemList().stream().map(CartItem::getProduct).toList();
        Assertions.assertThat(productListBeforeDelete.contains(expectedProduct)).isTrue();
        cartService.deleteCartItem(accessToken, expectedProduct.getId());

        /* then */
        List<Product> userProductList = joinedUser.getCartItemList().stream().map(CartItem::getProduct).toList();
        Assertions.assertThat(userProductList.contains(expectedProduct)).isFalse();
    }
}