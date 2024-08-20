package com.clean_light.server.user.service;

import com.clean_light.server.user.dto.UserJoinRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserAuthService {

    private final PasswordEncoder passwordEncoder;

    public Long join(UserJoinRequest userJoinRequest) {
        String loginId = passwordEncoder.encode(userJoinRequest.getLoginId());
        String password = passwordEncoder.encode(userJoinRequest.getPassword());

        System.out.println("loginId = " + loginId);
        System.out.println("password = " + password);

        return 1L;
    }
}
