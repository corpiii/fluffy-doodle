package com.clean_light.server.auth.user.service;

import com.clean_light.server.auth.jwt.domain.TokenType;
import com.clean_light.server.auth.jwt.dto.UserTokenInfo;
import com.clean_light.server.auth.jwt.service.JwtService;
import com.clean_light.server.auth.user.domain.User;
import com.clean_light.server.auth.user.dto.UserAuthToken;
import com.clean_light.server.auth.user.error.UserAuthError;
import com.clean_light.server.auth.user.error.UserAuthException;
import com.clean_light.server.auth.user.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserAuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserInfoService userInfoService;

    public Long join(User user) {
        userInfoService.isExistLoginId(user.getLoginId());
        userInfoService.isExistNickName(user.getNickName());
        User savedUser = userRepository.save(user);

        return savedUser.getId();
    }

    public UserAuthToken login(User user) throws UserAuthException, JsonProcessingException {
        User foundedUser = userRepository.findByLoginId(user.getLoginId())
                .orElseThrow(() -> new UserAuthException(UserAuthError.INVALID_LOGIN_ID));

        if (!passwordEncoder.matches(user.getPassword(), foundedUser.getPassword())) {
            throw new UserAuthException(UserAuthError.INVALID_PASSWORD);
        }

        UserTokenInfo userTokenInfo = UserTokenInfo.from(foundedUser);
        String accessToken = jwtService.generateAccessToken(userTokenInfo);
        String refreshToken = jwtService.generateRefreshToken();
        String loginId = userTokenInfo.getLoginId();

        jwtService.sendToBlackListIfExist(loginId);
        jwtService.setToken(loginId, accessToken, refreshToken);

        return UserAuthToken.of(accessToken, refreshToken);
    }

    public void logout(String accessToken) throws JsonProcessingException {
        UserTokenInfo userTokenInfo = jwtService.decodeToken(accessToken);

        jwtService.sendToBlackListIfExist(userTokenInfo.getLoginId());
    }

    public void delete(String accessToken) throws JsonProcessingException {
        UserTokenInfo userTokenInfo = jwtService.decodeToken(accessToken);
        String loginId = userTokenInfo.getLoginId();

        jwtService.sendToBlackListIfExist(loginId);
        userRepository.deleteByLoginId(loginId);
    }

    public User findByLoginId(String loginId) {
        return userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UserAuthException(UserAuthError.USER_NOT_EXIST));
    }
}
