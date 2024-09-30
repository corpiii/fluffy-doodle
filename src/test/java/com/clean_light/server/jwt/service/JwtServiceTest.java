package com.clean_light.server.jwt.service;

import com.clean_light.server.jwt.dto.UserTokenInfo;
import com.clean_light.server.user.domain.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Slf4j
class JwtServiceTest {
    @Autowired JwtService jwtService;

    @Test
    @DisplayName("jwt 생성 후 decode 테스트")
    void generateAccessToken() throws JsonProcessingException {
        /* given */
        User user = User.builder()
                .loginId("loginId")
                .build();
        UserTokenInfo userTokenDTO = UserTokenInfo.from(user);

        /* when */
        String accessToken = jwtService.generateAccessToken(userTokenDTO);
        UserTokenInfo decodedToken = jwtService.decodeToken(accessToken);

        /* then */
        assertEquals(userTokenDTO.getLoginId(), decodedToken.getLoginId());
    }
}