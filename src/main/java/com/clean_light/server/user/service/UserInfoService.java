package com.clean_light.server.user.service;

import com.clean_light.server.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserInfoService {
    private final UserRepository userRepository;

    public boolean isExistLoginId(String loginId) {
        return userRepository.existsByLoginId(loginId);
    }

    public boolean isExistNickName(String nickName) {
        return userRepository.existsByNickName(nickName);
    }
}
