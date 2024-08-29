package com.clean_light.server.user.service;

import com.clean_light.server.jwt.dto.UserTokenInfo;
import com.clean_light.server.jwt.service.JwtService;
import com.clean_light.server.user.domain.User;
import com.clean_light.server.user.dto.UserAuthToken;
import com.clean_light.server.user.error.UserAuthError;
import com.clean_light.server.user.error.UserAuthException;
import com.clean_light.server.user.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@Transactional
@RequiredArgsConstructor
public class UserAuthService {
    private static final Duration ACCESS_EXPIRATION_TIME = Duration.ofMinutes(30);
    private static final Duration REFRESH_EXPIRATION_TIME = Duration.ofDays(7);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RedisTemplate<String, String> redisTemplate;

    public Long join(User user) {
        User savedUser = userRepository.save(user);

        return savedUser.getId();
    }

    public UserAuthToken login(User user) throws UserAuthException, JsonProcessingException {
        User foundedUser = userRepository.findByLoginId(user.getLoginId())
                .orElseThrow(() -> new UserAuthException(UserAuthError.INVALID_LOGIN_ID));

        if (!passwordEncoder.matches(user.getPassword(), foundedUser.getPassword())) {
            throw new UserAuthException(UserAuthError.INVALID_PASSWORD);
        }

        UserTokenInfo userTokenInfo = UserTokenInfo.from(user);
        String accessToken = jwtService.generateAccessToken(userTokenInfo, ACCESS_EXPIRATION_TIME);
        String refreshToken = jwtService.generateRefreshToken(REFRESH_EXPIRATION_TIME);
        UserAuthToken userAuthToken = UserAuthToken.of(accessToken, refreshToken);

        redisTemplate.opsForValue().set(user.getLoginId(), refreshToken, REFRESH_EXPIRATION_TIME);

        return userAuthToken;
    }

    public void logout(String accessToken) throws JsonProcessingException {
        // TODO
        // UserTokenInfo userTokenInfo = jwtService.decodeToken(accessToken);
    }
}
