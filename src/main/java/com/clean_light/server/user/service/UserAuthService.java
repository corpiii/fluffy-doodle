package com.clean_light.server.user.service;

import com.clean_light.server.user.domain.User;
import com.clean_light.server.user.error.UserAuthException;
import com.clean_light.server.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserAuthService {
    private final UserRepository userRepository;

    public Long join(User user) {
        User savedUser = userRepository.save(user);

        return savedUser.getId();
    }

    public String login(User user) throws UserAuthException {
        return "";
    }
}
