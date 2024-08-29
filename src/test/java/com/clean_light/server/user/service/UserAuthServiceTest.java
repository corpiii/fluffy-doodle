package com.clean_light.server.user.service;

import com.clean_light.server.user.domain.User;
import com.clean_light.server.user.dto.UserAuthToken;
import com.clean_light.server.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserAuthServiceTest {
    @Autowired UserAuthService userAuthService;
    @Autowired UserRepository userRepository;
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired RedisTemplate<String, String> redisTemplate;

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

    @Test
    @DisplayName("유저 로그인 테스트")
    @Transactional
    public void userLogin() throws Exception {
        /* given */
        String loginId = "loginId";
        String originPassword = "password";
        String encodedPassword = passwordEncoder.encode(originPassword);
        User willJoinUser = User.builder()
                .loginId(loginId)
                .password(encodedPassword)
                .email("email")
                .nickName("nickName")
                .build();

        userAuthService.join(willJoinUser);

        /* when */
        User willLoginUser = User.builder()
                .loginId(loginId)
                .password(originPassword)
                .build();

        UserAuthToken userAuthToken = userAuthService.login(willLoginUser);
        String accessToken = userAuthToken.getAccessToken();
        userAuthService.login(willLoginUser);

        /* then */
        String refreshToken = redisTemplate.opsForValue().get(loginId);
        assertNotNull(refreshToken);
    }

    @Test
    @DisplayName("유저 로그아웃 테스트")
    @Transactional
    public void userLogout() throws Exception {
        /* given */
        String loginId = "loginId";
        String originPassword = "password";
        String encodedPassword = passwordEncoder.encode(originPassword);
        User willJoinUser = User.builder()
                .loginId(loginId)
                .password(encodedPassword)
                .email("email")
                .nickName("nickName")
                .build();

        User willLoginUser = User.builder()
                .loginId(loginId)
                .password(originPassword)
                .build();

        userAuthService.join(willJoinUser);
        UserAuthToken userAuthToken = userAuthService.login(willLoginUser);
        String accessToken = userAuthToken.getAccessToken();

        /* when */
        userAuthService.logout(accessToken);

        /* then */
        String refreshToken = redisTemplate.opsForValue().get(loginId);
        assertNull(refreshToken);
    }
}