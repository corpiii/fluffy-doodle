package com.clean_light.server.user.service;

import com.clean_light.server.user.error.UserAuthError;
import com.clean_light.server.user.error.UserAuthException;
import com.clean_light.server.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
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
