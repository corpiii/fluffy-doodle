package com.clean_light.server.cart.service;

import com.clean_light.server.auth.jwt.domain.TokenType;
import com.clean_light.server.auth.jwt.dto.UserTokenInfo;
import com.clean_light.server.auth.jwt.service.JwtService;
import com.clean_light.server.auth.user.domain.User;
import com.clean_light.server.auth.user.repository.UserRepository;
import com.clean_light.server.auth.user.service.UserAuthService;
import com.clean_light.server.cart.domain.CartItem;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartService {
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public List<CartItem> fetchCartItemListBy(String accessToken) throws JsonProcessingException {
        UserTokenInfo userTokenInfo = jwtService.decodeToken(accessToken);
        String loginId = userTokenInfo.getLoginId();

        if (!jwtService.isExist(loginId, TokenType.ACCESS)) {
            throw new IllegalArgumentException("해당 유저를 찾을 수 없습니다. 다시 로그인 해주세요.");
        }

        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다. 다시 로그인 해주세요."));
        return user.getCartItemList();
    }
}
