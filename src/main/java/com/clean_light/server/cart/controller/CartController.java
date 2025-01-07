package com.clean_light.server.cart.controller;

import com.clean_light.server.auth.jwt.domain.TokenType;
import com.clean_light.server.auth.jwt.dto.UserTokenInfo;
import com.clean_light.server.auth.jwt.service.JwtService;
import com.clean_light.server.auth.user.error.UserAuthError;
import com.clean_light.server.auth.user.error.UserAuthException;
import com.clean_light.server.cart.domain.CartItem;
import com.clean_light.server.auth.user.domain.User;
import com.clean_light.server.auth.user.service.UserAuthService;
import com.clean_light.server.cart.dto.CartItemResponse;
import com.clean_light.server.global.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
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
    private final UserAuthService userAuthService;
    private final JwtService jwtService;

    @RequestMapping(value = "/api/user/cart", method = RequestMethod.HEAD)
    public ResponseEntity<ApiResponse<List<CartItemResponse>>> fetchCartItemList(HttpServletRequest request) {
        try {
            String accessToken = request.getParameter(HttpHeaders.AUTHORIZATION).substring(7);
            UserTokenInfo userTokenInfo = jwtService.decodeToken(accessToken);
            String loginId = userTokenInfo.getLoginId();

            if (!jwtService.isExist(loginId, TokenType.ACCESS)) {
                throw new IllegalArgumentException("해당 유저를 찾을 수 없습니다. 다시 로그인 해주세요.");
            }

            User user = userAuthService.findByLoginId(loginId);
            List<CartItem> cartItemList = user.getCartItemList();
            List<CartItemResponse> responseList = cartItemList.stream().map(CartItemResponse::new).toList();

            return ResponseEntity.ok(new ApiResponse<>(true, responseList, ""));
        } catch (Exception exception) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, null, exception.getMessage()));
        }
    }

}
