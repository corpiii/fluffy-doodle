package com.clean_light.server.user.service;

import com.clean_light.server.user.domain.User;
import com.clean_light.server.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserAuthServiceTest {
    @Autowired UserAuthService userAuthService;
    @Autowired UserRepository userRepository;

    @Test
    @DisplayName("유저 생성 성공 테스트(값의 검증은 컨트롤러에서 시도)")
    @Transactional
    public void userJoin() throws Exception {
        /* given */
        User willJoinUser = User.builder()
                .loginId("loginId")
                .password("password")
                .email("email")
                .nickName("nickName")
                .build();

        /* when */
        userAuthService.join(willJoinUser);

        /* then */
        User user = userRepository.findByLoginId(willJoinUser.getLoginId()).get();

        assertNotNull(user);
        assertEquals(user.getLoginId(), willJoinUser.getLoginId());
        assertEquals(user.getPassword(), willJoinUser.getPassword());
        assertEquals(user.getEmail(), willJoinUser.getEmail());
        assertEquals(user.getNickName(), willJoinUser.getNickName());
    }
}