package com.clean_light.server.user.service;

import com.clean_light.server.user.error.UserAuthError;
import com.clean_light.server.user.error.UserAuthException;
import com.clean_light.server.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserInfoService {
    private final UserRepository userRepository;

    public boolean isExistLoginId(String loginId) {
        if (userRepository.existsByLoginId(loginId)) {
            throw new UserAuthException(UserAuthError.DUPLICATED_LOGIN_ID);
        }

        return true;
    }

    public boolean isExistNickName(String nickName) {
        if (userRepository.existsByNickName(nickName)) {
            throw new UserAuthException(UserAuthError.DUPLICATED_NICK_NAME);
        }

        return true;
    }
}
