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

@Service
@Transactional
@RequiredArgsConstructor
public class UserAuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RedisTemplate<String, String> redisTemplate;
    private final UserInfoService userInfoService;

    public Long join(User user) {
        userInfoService.isExistLoginId(user.getLoginId());
        userInfoService.isExistNickName(user.getNickName());
        User savedUser = userRepository.save(user);

        return savedUser.getId();
    }

    @Transactional(readOnly = true)
    public UserAuthToken login(User user) throws UserAuthException, JsonProcessingException {
        User foundedUser = userRepository.findByLoginId(user.getLoginId())
                .orElseThrow(() -> new UserAuthException(UserAuthError.INVALID_LOGIN_ID));

        if (!passwordEncoder.matches(user.getPassword(), foundedUser.getPassword())) {
            throw new UserAuthException(UserAuthError.INVALID_PASSWORD);
        }

        UserTokenInfo userTokenInfo = UserTokenInfo.from(foundedUser);
        String accessToken = jwtService.generateAccessToken(userTokenInfo);
        String refreshToken = jwtService.generateRefreshToken();
        UserAuthToken userAuthToken = UserAuthToken.of(accessToken, refreshToken);

        redisTemplate.opsForValue().set(userTokenInfo.getId().toString(), refreshToken, JwtService.REFRESH_EXPIRATION_TIME);

        return userAuthToken;
    }

    public void logout(String accessToken) throws JsonProcessingException {
        UserTokenInfo userTokenInfo = jwtService.decodeToken(accessToken);
        redisTemplate.opsForValue().getAndDelete(userTokenInfo.getId().toString());

        // 로그아웃 이후 access 블랙리스트 처리?
    }

    public void delete(String accessToken) throws JsonProcessingException {
        UserTokenInfo userTokenInfo = jwtService.decodeToken(accessToken);
        Long id = userTokenInfo.getId();

        redisTemplate.opsForValue().getAndDelete(id.toString());
        userRepository.deleteById(id);

        // 탈퇴 이후 accessToken 블랙리스트 처리?
    }
}
