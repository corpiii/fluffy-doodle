package com.clean_light.server.jwt.service;

import com.clean_light.server.jwt.dto.UserTokenDTO;
import com.clean_light.server.user.domain.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

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
        UserTokenDTO userTokenDTO = UserTokenDTO.from(user);

        /* when */
        String accessToken = jwtService.generateAccessToken(userTokenDTO);
        UserTokenDTO decodedToken = jwtService.decodeToken(accessToken);

        /* then */
        assertEquals(userTokenDTO.getLoginId(), decodedToken.getLoginId());
    }
}